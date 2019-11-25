import org.eclipse.jdt.core.dom.rewrite.ASTRewrite
import parser.converters.{DoStatementCon, ForStatementCon, WhileStatementCon}
import parser.instrumentation.AssignmentInstrum
import parser.utils.{ASTParserLocal, FileWriter}

object Main extends App {
  if (args.length < 3){
    println("Please pass source file, sources path and output file")
  }
  val fileName = args(0)
  val sources = args(1)
  val outputFile = args(2)

  val parser = ASTParserLocal.getParser(sources, "", fileName)

  val originalCode = FileWriter.readFile(fileName)
  val cu = ASTParserLocal.getCU(parser, originalCode)
  val rewriter: ASTRewrite = ASTRewrite.create(cu.getAST)

  val whileRewriter = new WhileStatementCon(cu, rewriter).startBlockConvert()
  val whileCode = FileWriter.getSourceCodeAsString(whileRewriter, originalCode)
  val whileCU = ASTParserLocal.getCU(parser, whileCode)

  val doRewriter = new DoStatementCon(whileCU, whileRewriter).startBlockConvert()
  val doCode = FileWriter.getSourceCodeAsString(doRewriter, whileCode)
  val doCU = ASTParserLocal.getCU(parser, doCode)

  val forRewriter = new ForStatementCon(doCU, doRewriter).startBlockConvert()
  val forCode = FileWriter.getSourceCodeAsString(forRewriter, doCode)
  val forCU = ASTParserLocal.getCU(parser,forCode)

  val newRewriter = ASTRewrite.create(forCU.getAST)
  val assignmentRewriter = new AssignmentInstrum(forCU,newRewriter).startInstrum()

  FileWriter.writeFile(assignmentRewriter, fileName,outputFile)


}
