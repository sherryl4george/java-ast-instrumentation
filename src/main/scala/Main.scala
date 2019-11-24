import org.eclipse.jdt.core.dom._
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

  val astParser: ASTParserLocal = new ASTParserLocal
  val cu: CompilationUnit = astParser.getCU(sources,
    "/usr/bin/java/lib/rt.jar",
    fileName)
  val rewriter: ASTRewrite = ASTRewrite.create(cu.getAST)
  val whileRewriter = new WhileStatementCon(cu, rewriter).startBlockConvert()
  val doRewriter = new DoStatementCon(cu, whileRewriter).startBlockConvert()
  val forRewriter = new ForStatementCon(cu, doRewriter).startBlockConvert()
  FileWriter.writeFile(forRewriter, fileName,outputFile)

  val cu1: CompilationUnit = astParser.getCU(sources,
    "/usr/bin/java/lib/rt.jar",
    outputFile)
  val newRewriter = ASTRewrite.create(cu1.getAST)
  val assignmentRewriter = new AssignmentInstrum(cu1,newRewriter).startInstrum()
  FileWriter.writeFile(assignmentRewriter, outputFile,outputFile+"1")
}
