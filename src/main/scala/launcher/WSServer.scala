package launcher

import java.nio.file.Paths
import java.time.format.DateTimeFormatter
import java.time.{LocalDateTime, ZoneId}

import akka.NotUsed
import akka.actor._
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server.Directives.{complete, get, handleWebSocketMessages, path, pathEndOrSingleSlash}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink}
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import launcher.InstrumActor.{Ack, Complete, Init, Parse}
import parser.utils.FileHelper
import play.api.libs.json.{JsArray, Json}

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.io.StdIn
import scala.language.postfixOps

/**
  * Object for the InstrumActor
  * Has case object and class to represent each action
  * Init - Used when a new connection is mad
  * Act - Used to send ack back to the client
  * Complete - USed to notify the teardown of a single client
  * Parse - Used to notify each new input coming in
  */
object InstrumActor {
  case object Init
  case object Ack
  case object Complete
  case class Parse(value: Seq[String])
}

/**
  * Actor class used to represent each connection to the client
  * Each client is represeneted by a separate instance of this class
  * Hence all data variables are separate for each running instance
  * This help to segregate and write data into separate files easily without
  * much post-processing.
  * Each jvm instance qualifies as a new client
  */
class InstrumActor extends Actor with LazyLogging{
  // Hashtable to store the binding and value for each binding
  val dataStore: mutable.HashMap[String,List[BindingData]] = mutable.HashMap.empty[String,List[BindingData]]
  // Running trace to be store here
  val trace: mutable.StringBuilder = new mutable.StringBuilder()
  val config = ConfigFactory.load("server.conf")
  /**
    * Parse each JSON object that is obtained from the client
    * Extract elements according to the defined JSON schema
    * An instance level hastable is maintained for each client and the bindings of each
    * name is saved as a list
    * Also a running variable is used to save the trace, which is whenever a new instrum statement
    * is invoked
    * @param jsonString - Each JSON object sent from the client
    * @return
    */
  def parseJSONMessage(jsonString: String)   = {
    logger.trace("Parsing json message: " + jsonString)
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

  /**
    * This will be triggered when the socket connection is tore down
    * This means that there is no more data to come in
    * Time to write the hashtable, formatted and the running trace into
    * a txt file
    * All files are created with timestamped names and written into the tracefiles
    */
  def writeData(): Unit = {
    logger.info("Writing traces into file - Started")
    val bindings = new mutable.StringBuilder()
    // use datastore and get the values from the list and make it to a string
    dataStore.foreach{case(key, value)=>{
      bindings.append(s"\n========= $key =========\n${value.mkString("\n")}\n")
    }}
    dataStore.clear()

    // Create a file name based on the timestamp
    val fmt = DateTimeFormatter.ofPattern("uuuu_MM_dd_HH_mm_ss_SSS")
    val time = LocalDateTime.now(ZoneId.of(config.getString("timezone"))).format(fmt)
    val fileName = time + ".txt"
    logger.info("Trace written into "+ fileName)
    FileHelper.writeFile(trace.toString, Paths.get(config.getString("traceDir"),"trace_"+fileName).toString)
    FileHelper.writeFile(bindings.toString, Paths.get(config.getString("traceDir"),"bindings_"+fileName).toString)
    trace.clear()
    logger.info("Writing traces into file - Ended")
  }

  /**
    * Override receive to handle different events coming from the client
    * Init, Parse, Complete are handled
    * @return
    */
  override def receive: Receive = {
    case Init =>
      sender ! Ack
    case Parse(value) =>
      value.map(parseJSONMessage)
      sender ! Ack
    case Complete => {
      writeData()
      println(s"WebSocket terminated")
      logger.info("WebSocket terminated")
    }
  }
}

/**
  * Akka-websocket server
  * Server acts as sink for all data coming from the JVM Launcher
  * Based on Actor and Flows
  */
class WSServer extends LazyLogging {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  // Actor for each client
  val instrumActor = system.actorOf(Props[InstrumActor], "instrumactor")

  // Read server conf.
  val config = ConfigFactory.load("server.conf")

  /**
    * Create a flow here
    * Handle TextMessages, Grouped to 1000 and pass it to the actor
    * Send Ack once the work is done.
    */
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
      .map(messages => Parse(messages))
      .alsoTo(sink)
      .map(_ => TextMessage("Ack"))

  /**
    * Set the root
    * Webwsockets are available at /instrumserver
    */
  import akka.http.scaladsl.server.RouteConcatenation._
  val route = get {
    pathEndOrSingleSlash {
      complete("Welcome to websocket server")
    }
  } ~ path("instrumserver") {
    logger.trace("Got data on WS")
    // Set sink and give it to the Actor
    val sink = Sink.actorRefWithAck(instrumActor, Init, Ack, Complete)
    // Handle the messages using the actor
    handleWebSocketMessages(echoService(sink))
  }
  //Start the server here
  val interface = config.getString("interface")
  val port = config.getInt("port")
  val bindingFuture =
    Http().bindAndHandle(route, interface = interface, port = port)
  logger.info(s"Webserver stareted http://$interface:$port/")
  println(s"Server online at http://$interface:$port/\nPress RETURN to stop...")
  StdIn.readLine()

  // Stop server if a readLine() is met
  import system.dispatcher // for the future transformations
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done
  logger.info("Webserver stopped")
}