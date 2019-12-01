import java.io.File
import java.nio.file.Paths

import org.apache.commons.io.FileUtils
import parser.converters.{BlockConverter, DoStatementCon, FinalConverter, ForStatementCon, WhileStatementCon}
import parser.instrumentation._
import parser.utils.{ASTParserLocal, FileHelper}

object Main extends App {

  if (args.length < 1){
    println("Please pass source file, sources path and output file")
  }

  val sources = args(0)
  val src = new File(sources)
  val oldSrc = new File(Paths.get(src.getParent, "old_src").toUri)
  FileUtils.forceMkdir(oldSrc)
  FileHelper.getJavaFiles(sources).map(instrumentBegin(sources,_))

  def instrumentBegin(str: String, file: File) : Unit = {

    val fileName = file.getAbsolutePath
    val cu = ASTParserLocal.getParser(sources, "", fileName, "")
    val originalCode = FileHelper.readFile(fileName)

    val blockRewriter = new BlockConverter(cu).startBlockConvert()
    val blockCode = FileHelper.getSourceCodeAsString(blockRewriter, originalCode)
    val blockCU = ASTParserLocal.getParser(sources, "", fileName, blockCode)

    val whileRewriter = new WhileStatementCon(blockCU).startBlockConvert()
    val whileCode = FileHelper.getSourceCodeAsString(whileRewriter, blockCode)
    val whileCU = ASTParserLocal.getParser(sources, "", fileName, whileCode)

    val doRewriter = new DoStatementCon(whileCU).startBlockConvert()
    val doCode = FileHelper.getSourceCodeAsString(doRewriter, whileCode)
    val doCU = ASTParserLocal.getParser(sources, "", fileName, doCode)

    val forRewriter = new ForStatementCon(doCU).startBlockConvert()
    val forCode = FileHelper.getSourceCodeAsString(forRewriter, doCode)
    val forCU = ASTParserLocal.getParser(sources, "", fileName, forCode)

    FileHelper.writeFile( forCode ,fileName)

    val finalRewriter = new Instrum(forCU).startInstrum()
    val finalCode = FileHelper.getSourceCodeAsString(finalRewriter,forCode)
    val finalCU = ASTParserLocal.getParser(sources,"",fileName,finalCode)

    val completeRewriter = new FinalConverter(finalCU).startInstrum()
    val completeCode = FileHelper.getSourceCodeAsString(completeRewriter,finalCode)

    FileUtils.copyFileToDirectory(file,oldSrc)
    FileHelper.writeFile( completeCode ,fileName)
  }

}
