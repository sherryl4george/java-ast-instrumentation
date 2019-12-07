package launcher

import java.io.File
import java.nio.file.Paths
import java.util

import scala.jdk.CollectionConverters._
import com.typesafe.config.Config
import org.apache.commons.io.FileUtils
import org.apache.tools.ant.{BuildEvent, DefaultLogger, Project, ProjectHelper}
import parser.utils.{FileHelper, utils}

case class AntBuilder(config: Config){
  class MyLogger extends DefaultLogger{
    override def buildFinished(event: BuildEvent): Unit = {
      println("BUILD FINISHED")
      Thread.sleep(1000)
      executeJava()
    }

    def executeJava(): Unit = {
      val confFile = new File("src/main/resources/File.conf")
      FileUtils.touch(confFile)
      val root = config.getString("compile.root")
      val targetDir = Paths.get(root,config.getString("compile.targetDir")).toString
      val jars = Paths.get(root,config.getString("compile.jarFolder")).toString
      val jarList = FileHelper.getFilesByExtension(jars,"jar").mkString(":")
      val main = config.getString("run.mainClass")
      println(config.getAnyRefList("run.arguments").size())
      config.getAnyRefList("run.arguments").forEach(x => invoke(targetDir,jarList,main,x.asInstanceOf[util.ArrayList[String]].asScala.toList.mkString(" "),confFile.toString))
    }

    def invoke(target:String, jarList: String, mainClass : String, argList : String, confFile:String) = {
      val lines =  "fileName = " + utils.wrapStringInQuotes(mainClass.replace(".","_") + "_" + AntBuilder.increment())
      println("maithreyi" + lines)
      FileHelper.writeFile(lines,confFile)
      val map = Map(
        "main" -> (mainClass + " " + argList),
        "options" -> ("-classpath " + target + ":" + jarList)
      )
      val jvmLauncher = JVMLauncher()
      jvmLauncher.init(map)
      jvmLauncher.start()
      jvmLauncher.shutdown()
    }
  }

  def compileAndLaunchJVM(): Unit = { // File buildFile = new File("build.xml");
    val buildFile = Paths.get(config.getString("compile.root"),config.getString("compile.buildFile")).toFile
  //  config.get
    val p = new Project
    p.setUserProperty("ant.file", buildFile.getAbsolutePath)
    val consoleLogger = new MyLogger
    consoleLogger.setErrorPrintStream(System.out)
    consoleLogger.setOutputPrintStream(System.out)
    consoleLogger.setMessageOutputLevel(Project.MSG_INFO)
    p.addBuildListener(consoleLogger)
    try {
      p.fireBuildStarted()
      p.init()
      val helper = ProjectHelper.getProjectHelper
      p.addReference("ant.projectHelper", helper)
      helper.parse(p, buildFile)
      p.executeTarget(p.getDefaultTarget)
      p.fireBuildFinished(null)
    } catch {
      case e: Exception => println("Message Failed ->" + e.getMessage)

    }
  }
}

object AntBuilder{
  private[this] var variable = 0

  /**
   * increments and returns a new variable for every execution.
   * @return
   */
  def increment():String = {
    variable = variable + 1
    variable.toString
  }
}

