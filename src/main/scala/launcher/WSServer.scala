package launcher

import java.nio.file.Paths
import java.time.{LocalDateTime, ZoneId}
import java.time.format.DateTimeFormatter

import akka.NotUsed
import akka.actor._
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{BinaryMessage, Message, TextMessage}
import akka.http.scaladsl.server.Directives.{complete, get, handleWebSocketMessages, path, pathEndOrSingleSlash}
import akka.http.scaladsl.server.PathMatcher
import akka.http.scaladsl.server.PathMatchers.LongNumber
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink}
import com.typesafe.config.ConfigFactory
import launcher.Total.{Ack, Complete, Init, Parse}
import parser.utils.FileHelper
import play.api.libs.json.{JsArray, Json}

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.io.StdIn
import scala.language.postfixOps

object Total {
  case object Init
  case object Ack
  case object Complete
  case class Parse(value: Seq[String], lastMessage: Long)
}

class Total extends Actor {
  val dataStore: mutable.HashMap[String,List[BindingData]] = mutable.HashMap.empty[String,List[BindingData]]
  val trace: mutable.StringBuilder = new mutable.StringBuilder()

  def parseJSONMessage(jsonString: String)   = {
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
  }

  def writeData(): Unit = {
    val bindings = new mutable.StringBuilder()
    dataStore.foreach{case(key, value)=>{
      bindings.append(s"\n========= $key =========\n${value.mkString("\n")}\n")
    }}
    dataStore.clear()
    //            println(dataStore)
    //    println(trace.toString)
    //    println(bindings.toString)

    val fmt = DateTimeFormatter.ofPattern("uuuu_MM_dd_HH_mm_ss_SSS")
    val time = LocalDateTime.now(ZoneId.of("America/New_York")).format(fmt)
    val fileName = time + ".txt"
    FileHelper.writeFile(trace.toString, Paths.get("tracefiles","trace_"+fileName).toString)
    FileHelper.writeFile(bindings.toString, Paths.get("tracefiles","bindings_"+fileName).toString)
    trace.clear()
  }
  override def receive: Receive = {
    case Init =>
      sender ! Ack
    case Parse(value, _) =>
      value.map(parseJSONMessage)
      sender ! Ack
    case Complete => {
      writeData()
      println(s"WebSocket terminated")
    }
  }
}

class WSServer {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  val total = system.actorOf(Props[Total], "total")

  val echoService = (sink: Sink[Parse, NotUsed]) =>
    Flow[Message]
      .collect {
        case TextMessage.Strict(text) =>
          Future.successful(text)
        case TextMessage.Streamed(textStream) =>
          textStream.runFold("")(_ + _)
            .flatMap(Future.successful)
      }
      .mapAsync(1)(identity)
      .groupedWithin(1000, 1 second)
      .map(messages => Parse(messages, 0))
      .alsoTo(sink)
      .map(_ => TextMessage("Ack"))

  import akka.http.scaladsl.server.RouteConcatenation._
  val route = get {
    pathEndOrSingleSlash {
      complete("Welcome to websocket server")
    }
  } ~ path("instrumserver") {
    val sink = Sink.actorRefWithAck(total, Init, Ack, Complete)
    handleWebSocketMessages(echoService(sink))
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