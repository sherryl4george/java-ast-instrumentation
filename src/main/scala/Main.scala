import org.eclipse.jdt.core.dom._
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite
import parser.converters.WhileStatementCon
import parser.utils.{ASTParserLocal, FileWriter}

class ForStatementVisitor extends ASTVisitor {
  var nodes:List[ForStatement] = List.empty
  override def visit(node: ForStatement): Boolean = {
    nodes = nodes.+:(node)
    super.visit(node)
  }

  def getAllFor():List[ForStatement] = nodes
}

//import java.util
//
//class ForStmt extends ASTVisitor {
//  val forst1 = new util.ArrayList[ForStmt]
//
//  override def visit(node: ForStmt): Boolean = {
//    forst1.add(node)
//    super.visit(node)
//  }
//
//  def getForst1: Nothing = forst1
//}


object Main extends App {
  val fileName = "/media/01D3908E9C0056A0/code/eclipse-workspace/cs474.test/src/cs474/test/Test.java"
  val astParser: ASTParserLocal = new ASTParserLocal
  val cu: CompilationUnit = astParser.getCU("/media/01D3908E9C0056A0/code/eclipse-workspace/cs474.test/src",
    "/usr/bin/java/lib/rt.jar",
    fileName)
  val rewriter: ASTRewrite = new WhileStatementCon(cu).startBlockConvert()
  FileWriter.writeFile(rewriter, fileName)
}
