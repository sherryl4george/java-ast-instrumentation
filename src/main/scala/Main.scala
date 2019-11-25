import parser.converters.{BlockConverter, DoStatementCon, ForStatementCon, WhileStatementCon}
import parser.instrumentation.{AssignmentInstrum, VDSInstrum}
import parser.utils.{ASTParserLocal, FileWriter}

object Main extends App {
  if (args.length < 3){
    println("Please pass source file, sources path and output file")
  }
  val fileName = args(0)
  val sources = args(1)
  val outputFile = args(2)

  val cu = ASTParserLocal.getParser(sources, "", fileName, "")
  val originalCode = FileWriter.readFile(fileName)

  val blockRewriter = new BlockConverter(cu).startBlockConvert()
  val blockCode = FileWriter.getSourceCodeAsString(blockRewriter, originalCode)
  val blockCU = ASTParserLocal.getParser(sources, "", fileName, blockCode)

  val whileRewriter = new WhileStatementCon(blockCU).startBlockConvert()
  val whileCode = FileWriter.getSourceCodeAsString(whileRewriter, blockCode)
//  println("jhjhj")
//  println(whileCode)
  val whileCU = ASTParserLocal.getParser(sources, "", fileName, whileCode)

  val doRewriter = new DoStatementCon(whileCU).startBlockConvert()
  val doCode = FileWriter.getSourceCodeAsString(doRewriter, whileCode)
  val doCU = ASTParserLocal.getParser(sources, "", fileName, doCode)

  val forRewriter = new ForStatementCon(doCU).startBlockConvert()
  val forCode = FileWriter.getSourceCodeAsString(forRewriter, doCode)
  val forCU = ASTParserLocal.getParser(sources, "", fileName, forCode)
  //  //
  val assignmentRewriter = new AssignmentInstrum(forCU).startInstrum()
  val assignmentCode = FileWriter.getSourceCodeAsString(assignmentRewriter, forCode)
  val assignmentCU = ASTParserLocal.getParser(sources, "", fileName, assignmentCode)

  val vdsRewriter = new VDSInstrum(assignmentCU).startInstrum()
  val vdsCode = FileWriter.getSourceCodeAsString(vdsRewriter, assignmentCode)
 // val assignmentCU = ASTParserLocal.getParser(sources, "", fileName, assignmentCode)


  FileWriter.writeFile( vdsCode ,outputFile)


}
