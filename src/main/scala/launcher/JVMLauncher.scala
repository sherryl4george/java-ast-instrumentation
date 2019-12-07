package launcher

import java.io.{IOException, InputStream}
import java.lang.System.err
import java.util

import com.sun.jdi.connect.{Connector, IllegalConnectorArgumentsException, LaunchingConnector, VMStartException}
import com.sun.jdi.{Bootstrap, VirtualMachine}
import com.sun.tools.jdi.SunCommandLineLauncher

import scala.jdk.CollectionConverters._

case class JVMLauncher(){
  private [this] var connector: LaunchingConnector = null
  private [this] var connectorArgs: util.Map[String, Connector.Argument] =  new util.HashMap[String,Connector.Argument]()
  private [this] var process: Process = null
  private [this] var jvm: VirtualMachine = null

  def init(arguments: Map[String, String]){
    val connectorEither = getConnector()
    getConnector match {
      case Right(newConnector) => connector = newConnector
      case Left(message) => throw new RuntimeException(message)
    }
    connectorArgs = initConnectArgs(arguments)
  }

  // Open JVM
  def start(): Unit = {
    launchTarget match {
      case Right(newJVM) => {
        jvm = newJVM
        jvm.setDebugTraceMode(1)
        if (jvm.canBeModified)
          setEventRequests(jvm)
      }
      case Left(message) => throw new RuntimeException(message)
    }
  }

  def shutdown(): Unit = {
    jvm.dispose()
  }

  private def initConnectArgs(arguments: Map[String, String]) = {
    val defaultArgs = connector.defaultArguments
    arguments.keys.foreach(key => {
      if(defaultArgs.containsKey(key)){
        val argument = defaultArgs.get(key)
        argument.setValue(arguments.get(key).get)
      }
    })
    println(defaultArgs)
    defaultArgs
  }

  private def launchTarget: Either[String, VirtualMachine] = {
    try {
      val vm = connector.launch(connectorArgs)
      process = vm.process
      displayRemoteOutput(process.getErrorStream)
      displayRemoteOutput(process.getInputStream)
      Right(vm)
    } catch {
      case ioe: IOException => Left("Unable to launch target VM." + ioe.getMessage)
      case icae: IllegalConnectorArgumentsException => Left("Internal debugger error. " + icae.getMessage)
      case vmse: VMStartException => {
        dumpFailedLaunchInfo(vmse.process)
        Left("Target VM failed to initialize. - vmstartexception: " + vmse.getMessage)
      }
    }
  }

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
        Left("Exception: Appropriate Connector not Found")
      }
    }
  }

  private def setEventRequests(vm: VirtualMachine): Unit = {
    val erm = vm.eventRequestManager
    val tsr = erm.createThreadStartRequest
    tsr.enable()
    val tdr = erm.createThreadDeathRequest
    tdr.enable()
  }

  // Start a thread responsible for input stream
  // read characters and print them out. (dumpStream)
  private def displayRemoteOutput(stream: InputStream): Unit = {
    val thr = new Thread("output reader") {
      override def run(): Unit = {
        try
          dumpStream(stream)
        catch {
          case ex: IOException => println("Failed reading output")
        }
      }
    }
    thr.setPriority(Thread.MAX_PRIORITY - 1)
    thr.start()
  }

  private def dumpFailedLaunchInfo(process: Process): Unit = {
    try {
      dumpStream(process.getErrorStream)
      dumpStream(process.getInputStream)
    } catch {
      case e: IOException =>
        err.println("Unable to display process output:" + e.getMessage)
    }
  }

  // Read every character from stream, output them
  private def dumpStream(stream: InputStream): Unit = println(scala.io.Source.fromInputStream(stream).mkString)
}
