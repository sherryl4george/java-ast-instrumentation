package launcher

import java.nio.file.Paths

import akka.actor._
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server.Directives.{complete, get, handleWebSocketMessages, path, pathEndOrSingleSlash}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import com.typesafe.config.ConfigFactory
import parser.utils.FileHelper
import play.api.libs.json.{JsArray, Json}

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.io.StdIn
import scala.util.{Failure, Success}

class WSServer {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  val dataStore: mutable.HashMap[String,List[BindingData]] = mutable.HashMap.empty[String,List[BindingData]]
  val trace: mutable.StringBuilder = new mutable.StringBuilder()

  val echoService: Flow[Message, Message, _] = Flow[Message].mapAsync(parallelism = 5) {
    case TextMessage.Strict(msg) => {
      Future.successful(TextMessage(parseJSONMessage(msg)))
    }

    case TextMessage.Streamed(stream) =>

      stream
        .limit(Int.MaxValue) // Max frames we are willing to wait for
        .completionTimeout(50 seconds) // Max time until last frame
        .runFold("")(_ ++ _) // Merges the frames
        .flatMap { (msg: String) => {
        Future.successful(TextMessage(parseJSONMessage(msg)))
      }
      }
  }

  def parseJSONMessage(jsonString: String) : String  = {
    val json = Json.parse(jsonString)
    val line = (json \ "line").get
    val seenAt = (json \ "statementType").get
    val data = (json \ "data").get
    data match {
      case JsArray(elements) => {
        val dataItems = elements.map(dataJson => {
          val itemType = (dataJson \ "type").get
          val name = (dataJson \ "name").get
          val value = (dataJson \ "value").get
          val bindingData = new BindingData(name.toString, value.toString, itemType.toString, line.toString, seenAt.toString)
          if(name.toString.length > 2 ) {
            val tempData: List[BindingData] = dataStore.get(name.toString).getOrElse(List.empty[BindingData])
            dataStore.put(name.toString(), tempData :+ bindingData)
          }
          bindingData.toShortString
        })
        trace.append(s"Line: $line, SeenAt: $seenAt\n${dataItems.mkString("\n")}\n========\n")
      }
      case _ =>
    }
    "Completed"
  }

  import akka.http.scaladsl.server.RouteConcatenation._
  val route = get {
    pathEndOrSingleSlash {
      complete("Welcome to websocket server")
    }
  } ~
    path("greeter") {
      val greeterRoute = echoService.watchTermination() { (_, done) =>
        done.onComplete {
          case Success(_) => {
            val bindings = new mutable.StringBuilder()
            dataStore.foreach{case(key, value)=>{
              bindings.append(s"\n========= $key =========\n${value.mkString("\n")}\n")
            }}
            dataStore.clear()
//            println(dataStore)
            println(trace.toString)
            println("******")
            println(bindings.toString)

            // Get current name to save
            val config = ConfigFactory.load("server/filename.conf")
            val fileName = config.getString("fileName")
            FileHelper.writeFile(trace.toString, Paths.get("tracefiles","trace_"+fileName).toString)
            FileHelper.writeFile(bindings.toString, Paths.get("tracefiles","bindings_"+fileName).toString)
            trace.clear()
          }
          case Failure(ex) =>
            println(s"Completed with failure : $ex")
        }
      }
      handleWebSocketMessages(greeterRoute)
    }
  //#websocket-request-handling
  val bindingFuture =
    Http().bindAndHandle(route, interface = "localhost", port = 8080)

  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine()

  import system.dispatcher // for the future transformations
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done


}


