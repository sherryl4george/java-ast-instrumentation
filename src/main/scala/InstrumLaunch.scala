import java.io.File
import java.nio.file.Paths

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import launcher.AntBuilder
import org.apache.commons.io.FileUtils
import parser.converters._
import parser.instrumentation._
import parser.utils.{ASTParserLocal, FileHelper}

import scala.io.StdIn

/**
 * The Main application - The starting point for the application.
 **/

object InstrumLaunch extends App with LazyLogging {
  getUserInput()

  /**
   * Get the user input to select a config file to run
   */
  def getUserInput(): Unit = {

    // Get all files in the resources/projectConf folder and ask the user which project to run
    println(
      """
        |***************************************************************************************************************
        |                                         Java Instrumentation Application
        |***************************************************************************************************************
        |Choose a config file from below to run the instrumentation
        |
        |To add a new project to the list add a new .conf file to the resources/projectConf folder with details as
        |specified in the documentation
        |
        |***************************************************************************************************************
        |""".stripMargin)

    val confFileList = FileHelper.getFilesByExtension(Paths.get(System.getProperty("user.dir"), "config/instrum").toString, "conf").zipWithIndex
    confFileList.foreach {
      case (el, i) => println(i + 1 + ": " + el.getName)
    }
    println(0 + ": Exit")
    try {
      val option = StdIn.readInt()
      if (option < 0 || option > confFileList.length) {
        logger.warn("Option is invalid. Enter a value between 0 and " + confFileList.length)
        println(s"\nPlease enter a value between 0 and ${confFileList.length}")
        getUserInput()
      }
      else if (option == 0) {
        logger.info("Exiting.")
        sys.exit()
      } else {
        logger.info("Reading config " + confFileList(option-1)._1.toString)
        driver(confFileList(option - 1)._1.toString)
      }
    }
    catch {
      case _: NumberFormatException =>
        logger.warn("Invalid value entered. Try again!")
        println("\nInvalid value entered. Please try again!\n")
    }
  }

  /**
   * Initialize and read configuration as needed.
   * Invoke instrumentation
   * Compile and Run JVM
   */
  def driver(configPath: String) = {

    logger.debug("Begin instrumenting the project from config =>" + configPath)

    //Retrieve the config file.
    val config = ConfigFactory.parseFile(new File(configPath))

    //Read sources
    val rootRelativetoInstrumDir: Boolean = config.getBoolean("compile.rootRelativetoInstrumDir")

    // Get the correct path
    val root = if (rootRelativetoInstrumDir)
      Paths.get(System.getProperty("user.dir"), config.getString("compile.root")).toString
    else
      config.getString("compile.root")

    //Get the sources and jars directory
    val sources = Paths.get(root, config.getString("compile.srcDir")).toString
    val jarsDirDest = Paths.get(root, config.getString("compile.jarFolder")).toString
    val src = new File(sources)
    val astParserDest = Paths.get(sources, "astparser").toFile

    //Get the AST Parser directory. This is where the TemplateClass.java (holds instrum method) file is stored.
    val astParserDirSrc = new File(Paths.get(System.getProperty("user.dir"), "config/astparser").toString)

    //Get the folder where jars are save
    val jarsDirSrc = Paths.get(System.getProperty("user.dir"), "config/dependencyjar").toString

    // Delete ast parser directory. This will be recreated later.
    FileUtils.deleteDirectory(astParserDest)

    // Create the old sources directory to move the original source.
    val oldSrc = new File(Paths.get(src.getParent, "oldSrc").toUri)
    FileUtils.forceMkdir(oldSrc)

    //Copy jars used by instrumenter before proceeding
    FileUtils.copyDirectory(new File(jarsDirSrc), new File(jarsDirDest), true)

    // Begin instrumentation for each Java file in the sources directory.
    FileHelper.getFilesByExtension(sources, "java").map(instrumentBegin(sources, oldSrc, _))

    // Copy fresh ast parser directory to source folder. This is for the purpose of executing the instrumented source application.
    FileUtils.copyDirectoryToDirectory(astParserDirSrc, src)

    //Compile and launch JVM.
    AntBuilder(config).compileAndLaunchJVM()

    logger.debug("Done with instrumentation and execution for project config " + configPath)
  }


  /**
   *
   * The method that instruments a Java file.
   *
   * @param src
   * @param file
   */
  def instrumentBegin(src: String, oldSrc: File, file: File): Unit = {

    //Reads the source as a string and obtains the compilation unit from the AST of this source.
    val fileName = file.getAbsolutePath
    val cu = ASTParserLocal.getCU(src, "", fileName, "")
    val originalCode = FileHelper.readFile(fileName)
    logger.trace("Original code " + originalCode)

    /** Convert all single lines to blocks and get the modified compilation unit.
     * if(expression)
     * i++;
     * modified to
     * if(expression) {
     * i++; }
     */

    val blockRewriter = new BlockConverter(cu).startBlockConvert()
    val blockCode = FileHelper.getSourceCodeAsString(blockRewriter, originalCode)
    val blockCU = ASTParserLocal.getCU(src, "", fileName, blockCode)
    logger.trace("Block rewritten code " + blockCode)
    /**
     * while statements are rewritten and a modified compilation unit is obtained.
     * while(X() < 2){
     * i++;
     * }
     * modified to
     * int wh1 = X();
     * while(wh1 < 2) {
     * i++;
     * wh1 = X();
     * }
     */

    val whileRewriter = new WhileStatementCon(blockCU).startBlockConvert()
    val whileCode = FileHelper.getSourceCodeAsString(whileRewriter, blockCode)
    val whileCU = ASTParserLocal.getCU(src, "", fileName, whileCode)
    logger.trace("While Rewritten code " + whileCode)

    /**
     * Do-while statements are rewritten and a modified compilation unit is obtained.
     * do{
     * i++;
     * } while(X() < 2);
     * modified to
     * int wh1 = X();
     * do {
     * i++;
     * wh1 = X();
     * } while(wh1 < 2);
     */
    val doRewriter = new DoStatementCon(whileCU).startBlockConvert()
    val doCode = FileHelper.getSourceCodeAsString(doRewriter, whileCode)
    val doCU = ASTParserLocal.getCU(src, "", fileName, doCode)

    logger.trace("Do Rewritten code " + doCode)

    /**
     * For statements are rewritten and a modified compilation unit is obtained. This is the final re-write.
     * for(int i = 0; i < X() ; i++) {
     * val = i + 2;
     * }
     * modified to
     * int for1 = X();
     * for(int i = 0; i < for1 ; i++) {
     * val = i + 2;
     * for1 = X();
     */
    val forRewriter = new ForStatementCon(doCU).startBlockConvert()
    val forCode = FileHelper.getSourceCodeAsString(forRewriter, doCode)
    val forCU = ASTParserLocal.getCU(src, "", fileName, forCode)

    logger.trace("For Rewritten code " + forCode)

    //This rewritten source overwrites the original source as this is used as the input to the instrumentation.
    FileHelper.writeFile(forCode, fileName)

    //Begin instrumentation procedure to instrument and add logging statements.
    val finalRewriter = new Instrum(forCU).startInstrum()
    val finalCode = FileHelper.getSourceCodeAsString(finalRewriter, forCode)
    val finalCU = ASTParserLocal.getCU(src, "", fileName, finalCode)

    logger.trace("Final Rewritten code " + finalCode)

    //Final rewrite done with all the instrumented statements included in the source.
    val completeRewriter = new FinalConverter(finalCU).startInstrum()
    val completeCode = FileHelper.getSourceCodeAsString(completeRewriter, finalCode)

    //Move current source to old sources and rewrite the sources with the instrumented source file.
    FileUtils.copyFileToDirectory(file, oldSrc)
    FileHelper.writeFile(completeCode, fileName)
    logger.trace("Final Instrumented code " + completeCode)

  }

}
