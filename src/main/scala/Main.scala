import java.nio.file.{Files, Paths}

import javax.tools.JavaFileObject
import org.eclipse.jdt.core.JavaCore
import org.eclipse.jdt.core.dom._

import scala.io.Source
import org.eclipse.jdt.core.dom.ASTParser

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
//  val bufferedSource = Source.fromFile("/media/01D3908E9C0056A0/code/IdeaProjects/cs474.test/src/main/java/test.java")
  val bufferedSource = Source.fromFile(new String(Files.readAllBytes(Paths.get("/media/01D3908E9C0056A0/code/IdeaProjects/cs474.test/src/main/java/test.java"))))
  val lines = bufferedSource.getLines.mkString
  bufferedSource.close

  val parser = ASTParser.newParser(AST.JLS12);
  parser.setKind(ASTParser.K_COMPILATION_UNIT)
  val options = JavaCore.getOptions()
  parser.setCompilerOptions(options)
  val unitName = "test.java"
  parser.setUnitName(unitName)

  val sources = Array("/media/01D3908E9C0056A0/code/IdeaProjects/cs474.test/src")
  val classpath = Array("/usr/bin/java/lib/rt.jar")

  parser.setEnvironment(classpath, sources, Array[String]("UTF-8"), true)
  parser.setResolveBindings(true)
  parser.setBindingsRecovery(true)
  parser.setSource(lines.toCharArray)

  val cu = parser.createAST(null).asInstanceOf[CompilationUnit]

  if (cu.getAST.hasBindingsRecovery)
    System.out.println("Binding activated.")

  val v = new ForStatementVisitor
  cu.accept(v)

  val y = v.getAllFor().map(x => {
    val statement = x.asInstanceOf[Statement]
    statement.
  })
  println(y)
}
