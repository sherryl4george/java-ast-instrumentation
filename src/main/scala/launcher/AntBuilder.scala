package launcher

import java.nio.file.Paths
import java.util
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import org.apache.tools.ant.{BuildEvent, DefaultLogger, Project, ProjectHelper}
import parser.utils.FileHelper

import scala.jdk.CollectionConverters._

/**
 * The AntBuilder class.
 * This compiles and invokes the JVM using a JVM launcher
 * JVM is invoked for multiple inputs.
 * @param config
 */
case class AntBuilder(config: Config) extends LazyLogging{

  /**
   * The Default Logger class that uses a hook to identify the completion of the build.
   */
  class MyLogger extends DefaultLogger{
    /**
     * The buildFinished hook
     * @param event
     */
    override def buildFinished(event: BuildEvent): Unit = {
      println("BUILD FINISHED")
      logger.info("Build completed successfully!")

      //Invoke JVM once compilation has finished.
      executeJava()
    }

    /**
     * Invoke the JVM by passing appropriate parameters.
     */
    def executeJava(): Unit = {

      //Read appropriate values from config to invoke the JVM launch.
      val root = config.getString("compile.root")
      val targetDir = Paths.get(root,config.getString("compile.targetDir")).toString
      val jars = Paths.get(root,config.getString("compile.jarFolder")).toString
      val jarList = FileHelper.getFilesByExtension(jars,"jar").mkString(":")
      val main = config.getString("run.mainClass")

      //Invoke JVM on each input.
      val totalRuns = config.getAnyRefList("run.arguments").size()
      logger.info("Total number of inputs to run against - " + totalRuns)

      config.getAnyRefList("run.arguments").forEach(x => invoke(targetDir,jarList,main,x.asInstanceOf[util.ArrayList[String]].asScala.toList.mkString(" ")))
      logger.debug("Instrumented code executed on all inputs!")
    }

    /**
     * Launch the JVM with the set of inputs.
     * @param target
     * @param jarList
     * @param mainClass
     * @param argList
    0     */
    def invoke(target:String, jarList: String, mainClass : String, argList : String) = {
      logger.info("Invoking JVM with argument set -> " + argList)
      //Create arguments for invoking the JVM
      val map = Map(
        "main" -> (mainClass + " " + argList),
        "options" -> ("-classpath " + target + ":" + jarList)
      )

      //Launch the JVM
      val jvmLauncher = JVMLauncher()
      jvmLauncher.init(map)
      jvmLauncher.start()
      jvmLauncher.shutdown()
      Thread.sleep(1500)
    }
  }

  /**
   * The driver to compile the source code.
   */
  def compileAndLaunchJVM(): Unit = {
    //Read the build.xml file (ANT file)
    val buildFile = Paths.get(config.getString("compile.root"),config.getString("compile.buildFile")).toFile
    val p = new Project
    p.setUserProperty("ant.file", buildFile.getAbsolutePath)
    val consoleLogger = new MyLogger
    consoleLogger.setErrorPrintStream(System.out)
    consoleLogger.setOutputPrintStream(System.out)
    consoleLogger.setMessageOutputLevel(Project.MSG_VERBOSE)
    p.addBuildListener(consoleLogger)
    //Build the code.
    try {
      p.fireBuildStarted()
      p.init()
      val helper = ProjectHelper.getProjectHelper
      p.addReference("ant.projectHelper", helper)
      helper.parse(p, buildFile)
      p.executeTarget(p.getDefaultTarget)
      p.fireBuildFinished(null)
    } catch {
      case e: Exception =>
        e.printStackTrace()
        logger.error("Build failed with error -> " + e)
        println("Build Failed with error ->" + e)
    }
  }
}

/**
 * The static AntBuilder to generate unique file names for trace files per run.
 */
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

