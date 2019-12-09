package launcher

import java.io.{IOException, InputStream}
import java.lang.System.err
import java.util

import com.sun.jdi.connect.{Connector, IllegalConnectorArgumentsException, LaunchingConnector, VMStartException}
import com.sun.jdi.{Bootstrap, VirtualMachine}
import com.sun.tools.jdi.SunCommandLineLauncher
import com.typesafe.scalalogging.LazyLogging

import scala.jdk.CollectionConverters._

/**
 * The JVM launcher class.
 * Responsible for launching the JVM and execute the instrumented code.
 */
case class JVMLauncher() extends LazyLogging {
  private [this] var connector: LaunchingConnector = null
  private [this] var connectorArgs: util.Map[String, Connector.Argument] =  new util.HashMap[String,Connector.Argument]()
  private [this] var process: Process = null
  private [this] var jvm: VirtualMachine = null

  /**
   * Gets the JVM connector and creates connector arguments.
   * @param arguments
   */
  def init(arguments: Map[String, String]){
    val connectorEither = getConnector()
    getConnector match {
      case Right(newConnector) => connector = newConnector
      case Left(message) => {
        logger.error("Exception occured with JVM initialization => " + message + ". Cannot proceed!")
        throw new RuntimeException(message)
      }
    }
    connectorArgs = initConnectArgs(arguments)
  }

  /**
   * Start the JVM
   */
  def start(): Unit = {
    launchTarget match {
      case Right(newJVM) => {
        jvm = newJVM
        jvm.setDebugTraceMode(1)
        if (jvm.canBeModified)
          setEventRequests(jvm)
      }
      case Left(message) => {
        logger.error("Error occurred with JVM launch with message => " + message + ". Cannot proceed!")
        throw new RuntimeException(message)
      }
    }
  }

  /**
   * Shutdown the JVM
   */
  def shutdown(): Unit = {
    jvm.dispose()
  }

  /**
   * Initialize JVM connector arguments.
   * @param arguments
   * @return
   */
  private def initConnectArgs(arguments: Map[String, String]) = {
    val defaultArgs = connector.defaultArguments
    arguments.keys.foreach(key => {
      if(defaultArgs.containsKey(key)){
        val argument = defaultArgs.get(key)
        argument.setValue(arguments.get(key).get)
      }
    })
    println(defaultArgs)
    logger.info("JVM args - " + defaultArgs)
    defaultArgs
  }

  /**
   * Launch the JVM
   * @return
   */
  private def launchTarget: Either[String, VirtualMachine] = {
    try {
      val vm = connector.launch(connectorArgs)
      process = vm.process
      displayRemoteOutput(process.getErrorStream)
      displayRemoteOutput(process.getInputStream)
      logger.info("JVM launch complete")
      Right(vm)
    } catch {
      case ioe: IOException => {
        logger.error("JVM launch failed with exception -> " + ioe.getMessage)
        Left("Unable to launch target VM." + ioe.getMessage)
      }
      case icae: IllegalConnectorArgumentsException => {
        logger.error("Internal debugger error occurred -> " + icae.getMessage)
        Left("Internal debugger error. " + icae.getMessage)
      }
      case vmse: VMStartException => {
        dumpFailedLaunchInfo(vmse.process)
        logger.error("Target VM failed to initialize - vmstartException -> " + vmse.getMessage)
        Left("Target VM failed to initialize. - vmstartexception: " + vmse.getMessage)
      }
    }
  }

  /**
   * Get the JVM Connector.
   * @return
   */
  private def getConnector(): Either[String, LaunchingConnector] = {
    Bootstrap.virtualMachineManager.allConnectors.asScala
      .toList
      .filter(_ match {
        case _: SunCommandLineLauncher => true
        case _ => false
      })
      .head match {
      case x: SunCommandLineLauncher => Right(x.asInstanceOf[LaunchingConnector])
      case _ => {
        logger.error("Exception : Appropriate connector not found")
        Left("Exception: Appropriate Connector not Found")
      }
    }
  }

  /**
   * Sets Event Requests.
   * This is necessary to process various events to enable debugging etc.
   * @param vm
   */
  private def setEventRequests(vm: VirtualMachine): Unit = {
    val erm = vm.eventRequestManager
    val tsr = erm.createThreadStartRequest
    tsr.enable()
    val tdr = erm.createThreadDeathRequest
    tdr.enable()
  }

  /**
   * read characters and print them out. (dumpStream)
   * read characters and print them out. (dumpStream)
   * @param stream
   */
  private def displayRemoteOutput(stream: InputStream): Unit = {
    val thr = new Thread("output reader") {
      override def run(): Unit = {
        try
          dumpStream(stream)
        catch {
          case ex: IOException =>  {
            logger.error("Failed reading output")
            println("Failed reading output")
          }
        }
      }
    }
    thr.setPriority(Thread.MAX_PRIORITY - 1)
    thr.start()
  }

  /**
   * Dump Failure messages
   * @param process
   */
  private def dumpFailedLaunchInfo(process: Process): Unit = {
    try {
      dumpStream(process.getErrorStream)
      dumpStream(process.getInputStream)
    } catch {
      case e: IOException =>
        logger.error("Unable to display process output -> " + e.getMessage)
        err.println("Unable to display process output:" + e.getMessage)
    }
  }


  /**
   * Read every character from stream, output them
   * @param stream
   */
  private def dumpStream(stream: InputStream): Unit = println(scala.io.Source.fromInputStream(stream).mkString)
}
