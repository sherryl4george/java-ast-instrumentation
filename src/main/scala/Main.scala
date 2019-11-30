import java.io.File
import java.nio.file.Paths

import parser.converters.{BlockConverter, DoStatementCon, ForStatementCon, WhileStatementCon}
import parser.instrumentation.{AssignmentInstrum, ControlInstrum, ReturnInstrum, VDSInstrum}
import parser.utils.{ASTParserLocal, FileHelper}
import org.apache.commons.io.{FileUtils}

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

    val assignmentRewriter = new AssignmentInstrum(forCU).startInstrum()
    val assignmentCode = FileHelper.getSourceCodeAsString(assignmentRewriter, forCode)
    val assignmentCU = ASTParserLocal.getParser(sources, "", fileName, assignmentCode)

    val vdsRewriter = new VDSInstrum(assignmentCU).startInstrum()
    val vdsCode = FileHelper.getSourceCodeAsString(vdsRewriter, assignmentCode)
    val vdsCU = ASTParserLocal.getParser(sources, "", fileName, vdsCode)

    val returnRewriter = new ReturnInstrum(vdsCU).startInstrum()
    val returnCode = FileHelper.getSourceCodeAsString(returnRewriter, vdsCode)
    val returnCU = ASTParserLocal.getParser(sources, "", fileName, returnCode)

    val controlRewriter = new ControlInstrum(returnCU).startInstrum()
    val controlCode = FileHelper.getSourceCodeAsString(controlRewriter,returnCode)
    val controlCU = ASTParserLocal.getParser(sources,"",fileName,controlCode)


    FileUtils.copyFileToDirectory(file,oldSrc)
    FileHelper.writeFile( controlCode ,fileName)
  }

}
