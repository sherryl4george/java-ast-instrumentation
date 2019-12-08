import java.io.File
import java.nio.file.Paths

import ServerLaunch.getClass
import org.apache.commons.io.FileUtils
import parser.converters.{BlockConverter, DoStatementCon, FinalConverter, ForStatementCon, WhileStatementCon}
import parser.instrumentation._
import parser.utils.{ASTParserLocal, FileHelper}
import com.typesafe.scalalogging.LazyLogging
import com.typesafe.config.{ConfigFactory, ConfigList, ConfigRenderOptions}
import launcher.{AntBuilder, WSServer}

import scala.io.StdIn

/**
The Main application - The starting point for the application.
 **/
object Main extends App with LazyLogging {
  /**
   * We need to invoke the server and client for the program to run
   * the component to invoke is passed as a command line argument
   * Invoke the server or INstrumDriver based on which component is
   * requested
   */
  if(args.length != 0){
    args(0).toLowerCase match {
      case "server" => new WSServer
      case "instrum" => getUserInput()
      case _ => println("Invalid option: Valid options: server / instrum")
    }
  }
  else
    println("No parameters specified: Valid options: server / instrum")

  /**
   * Get the user input to select a config file to run
   */
  def getUserInput(): Unit = {
    // Get all files in the resources/projects folder and ask the user which project to run
    println(
      """
        |***************************************************************************************************************
        |                                         Java Instrumentation Application
        |***************************************************************************************************************
        |Choose a config file from below to run the instrumentation
        |
        |To add a new project to the list add a new .conf file to the resources/projects folder with details as
        |specified in the documentation
        |
        |***************************************************************************************************************
        |""".stripMargin)
    val confFileList = FileHelper.getFilesByExtension(getClass.getResource("projects").getPath, "conf").zipWithIndex
    confFileList.foreach {
      case(el, i) => println(i+1 + ": " + el.getName)
    }
    println(0 + ": Exit")
    try {
      val option = StdIn.readInt()
      if (option < 0 || option > confFileList.length) {
        println(s"\nPlease enter a value between 0 and ${confFileList.length}")
        getUserInput()
      }
      else if (option == 0)
        sys.exit()
      else
        driver(confFileList(option-1)._1.getName)
    }
    catch {
      case _: NumberFormatException =>
        println("\nInvalid value entered. Please try again!\n")
    }
  }

  /**
   * Initialize and read configuration as needed.
   * Invoke instrumentation
   * Compile and Run JVM
   */
  def driver(configPath: String) = {

    //Retrieve the config file.
    val config  = ConfigFactory.load(Paths.get("projects", configPath).toString)

    //Read sources
    val root = config.getString("compile.root")
    val sources = Paths.get(root,config.getString("compile.srcDir")).toString
    val jarsDir = Paths.get(root,config.getString("compile.jarFolder")).toString
    val src = new File(sources)

    //Get the AST Parser directory. This is where the TemplateClass.java (holds instrum method) file is stored.
    val astParserDir = getClass.getResource("astparser")

    //Get the folder where jars are save
    val dependencyJarsDir = getClass.getResource("dependencyjar")

    // Delete ast parser directory. This will be recreated later.
    FileUtils.deleteDirectory(new File(Paths.get(src.toString, "astparser").toUri))

    // Create the old sources directory to move the original source.
    val oldSrc = new File(Paths.get(src.getParent, "oldSrc").toUri)
    FileUtils.forceMkdir(oldSrc)

    //Copy jars used by instrumenter before proceeding
    FileUtils.copyDirectory(new File(dependencyJarsDir.toURI), new File(Paths.get(jarsDir).toString), true)

    // Begin instrumentation for each Java file in the sources directory.
    FileHelper.getFilesByExtension(sources,"java").map(instrumentBegin(sources,oldSrc,_))

    // Copy fresh ast parser directory to source folder. This is for the purpose of executing the instrumented source application.
    FileUtils.copyDirectoryToDirectory(new File(astParserDir.toURI), src)

    //Compile and launch JVM.
    AntBuilder(config).compileAndLaunchJVM()
  }


  /**
   *
   * The method that instruments a Java file.
   * @param src
   * @param file
   */
  def instrumentBegin(src: String, oldSrc : File, file: File) : Unit = {

    //Reads the source as a string and obtains the compilation unit from the AST of this source.
    val fileName = file.getAbsolutePath
    val cu = ASTParserLocal.getCU(src, "", fileName, "")
    val originalCode = FileHelper.readFile(fileName)

    /**Convert all single lines to blocks and get the modified compilation unit.
     * if(expression)
     * i++;
     * modified to
     * if(expression) {
     * i++; }
      */

    val blockRewriter = new BlockConverter(cu).startBlockConvert()
    val blockCode = FileHelper.getSourceCodeAsString(blockRewriter, originalCode)
    val blockCU = ASTParserLocal.getCU(src, "", fileName, blockCode)

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

    /**
     * Do-while statements are rewritten and a modified compilation unit is obtained.
     *  do{
     *  i++;
     *  } while(X() < 2);
     *  modified to
     *  int wh1 = X();
     *  do {
     *  i++;
     *  wh1 = X();
     *  } while(wh1 < 2);
     */
    val doRewriter = new DoStatementCon(whileCU).startBlockConvert()
    val doCode = FileHelper.getSourceCodeAsString(doRewriter, whileCode)
    val doCU = ASTParserLocal.getCU(src, "", fileName, doCode)

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

    //This rewritten source overwrites the original source as this is used as the input to the instrumentation.
    FileHelper.writeFile( forCode ,fileName)

    //Begin instrumentation procedure to instrument and add logging statements.
    val finalRewriter = new Instrum(forCU).startInstrum()
    val finalCode = FileHelper.getSourceCodeAsString(finalRewriter,forCode)
    val finalCU = ASTParserLocal.getCU(src,"",fileName,finalCode)

    //Final rewrite done with all the instrumented statements included in the source.
    val completeRewriter = new FinalConverter(finalCU).startInstrum()
    val completeCode = FileHelper.getSourceCodeAsString(completeRewriter,finalCode)

    //Move current source to old sources and rewrite the sources with the instrumented source file.
    FileUtils.copyFileToDirectory(file,oldSrc)
    FileHelper.writeFile( completeCode ,fileName)

  }

}
