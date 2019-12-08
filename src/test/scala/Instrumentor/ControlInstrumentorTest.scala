package Instrumentor

import org.eclipse.jdt.core.dom.Statement
import org.scalatest.FunSuite
import parser.instrumentation.ControlInstrum
import parser.utils.{ASTParserLocal, FileHelper}
import parser.visitors.{DoStatementVisitor, ForStatementVisitor, IfStatementVisitor, WhileStatementVisitor}

class ControlInstrumentorTest extends FunSuite{
  test("Control Instrument test for presence of control statements.") {
    val srcPath = getClass.getClassLoader.getResource("testSrc").getPath
    val srcFile = getClass.getClassLoader.getResource("testSrc/ControlInstrum.java").getPath
    val sourceCode = FileHelper.readFile(srcFile)
    val cu = ASTParserLocal.getCU(srcPath, "", srcFile, "")

    val forStatementVisitor = new ForStatementVisitor
    val ifStatementVisitor = new IfStatementVisitor
    val whileStatementVisitor = new WhileStatementVisitor
    val doStatementVisitor = new DoStatementVisitor

    cu.accept(forStatementVisitor)
    cu.accept(ifStatementVisitor)
    cu.accept(whileStatementVisitor)
    cu.accept(doStatementVisitor)
    var statements : List[Statement] = List()

    def createStatements(statement : Statement) = {
      statements = statements :+ statement
    }
    forStatementVisitor.getForStatements.map(createStatements(_))
    ifStatementVisitor.getIfStatements.map(createStatements(_))
    whileStatementVisitor.getWhileStatements.map(createStatements(_))
    val attributeList  = statements.map(new ControlInstrum().controlInstrumHelper(_))
    assert(attributeList.filter(_._2.contains("ForStatement")).size > 0)
  }

  test("Control Instrument test for absence of control statements.") {
    val srcPath = getClass.getClassLoader.getResource("testSrc").getPath
    val srcFile = getClass.getClassLoader.getResource("testSrc/ControlInstrum.java").getPath
    val sourceCode = FileHelper.readFile(srcFile)
    val cu = ASTParserLocal.getCU(srcPath, "", srcFile, "")

    val forStatementVisitor = new ForStatementVisitor
    val ifStatementVisitor = new IfStatementVisitor
    val whileStatementVisitor = new WhileStatementVisitor
    val doStatementVisitor = new DoStatementVisitor

    cu.accept(forStatementVisitor)
    cu.accept(ifStatementVisitor)
    cu.accept(whileStatementVisitor)
    cu.accept(doStatementVisitor)

    var statements : List[Statement] = List()

    def createStatements(statement : Statement) = {
      statements = statements :+ statement
    }
    forStatementVisitor.getForStatements.map(createStatements(_))
    ifStatementVisitor.getIfStatements.map(createStatements(_))
    whileStatementVisitor.getWhileStatements.map(createStatements(_))
    val attributeList  = statements.map(new ControlInstrum().controlInstrumHelper(_))
    assert(attributeList.filter(_._2.contains("DoStatement")).size == 0)

  }
}
