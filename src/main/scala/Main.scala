import java.io.File
import java.nio.file.Paths

import org.apache.commons.io.FileUtils
import parser.converters.{BlockConverter, DoStatementCon, FinalConverter, ForStatementCon, WhileStatementCon}
import parser.instrumentation._
import parser.utils.{ASTParserLocal, FileHelper}
import com.typesafe.scalalogging.LazyLogging
import com.typesafe.config.{ConfigFactory, ConfigList, ConfigRenderOptions}
import launcher.AntBuilder

/**
The Main application - The starting point for the application.
 **/
object Main extends App with LazyLogging {

  //Retrieve the config file.
  private final val config  = ConfigFactory.load("projects/project1.conf")

  //invoke the init method to initialize from config.
  driver()

  /**
   * Initialize and read configuration as needed.
   * Invoke instrumentation
   * Compile and Run JVM
   */
  def driver() = {
    //Read sources
//    val root = config.getString("compile.root")
//    val sources = Paths.get(root,config.getString("compile.srcDir")).toString
//    val src = new File(sources)
//
//    //Get the AST Parser directory. This is where the TemplateClass.java (holds instrum method) file is stored.
//    val astParserDir = config.getString("resources.astparser")
//
//    // Delete ast parser directory. This will be recreated later.
//    FileUtils.deleteDirectory(new File(Paths.get(src.toString, astParserDir).toUri))
//
//    // Create the old sources directory to move the original source.
//    val oldSrc = new File(Paths.get(src.getParent, config.getString("resources.oldSrc")).toUri)
//    FileUtils.forceMkdir(oldSrc)
//
//    //Begin instrumentation for each Java file in the sources directory.
//    FileHelper.getFilesByExtension(sources,"java").map(instrumentBegin(sources,oldSrc,_))
//
//    // Copy fresh ast parser directory to source folder. This is for the purpose of executing the instrumented source application.
//    FileUtils.copyDirectoryToDirectory(new File(getClass.getResource(astParserDir).toURI), src)

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
