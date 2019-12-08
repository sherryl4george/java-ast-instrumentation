package Instrumentor

import org.scalatest.FunSuite
import parser.instrumentation.AssignmentInstrum
import parser.utils.{ASTParserLocal, FileHelper}
import parser.visitors.ExpressionStatementVisitor

class ESInstrumTest extends FunSuite {
  test("Expression statements instrumented successfully.") {
    val srcPath = getClass.getClassLoader.getResource("testSrc").getPath
    val srcFile = getClass.getClassLoader.getResource("testSrc/MultipleInstrum.java").getPath
    val sourceCode = FileHelper.readFile(srcFile)
    val cu = ASTParserLocal.getCU(srcPath, "", srcFile, "")

    val expVisitor = new ExpressionStatementVisitor
    cu.accept(expVisitor)
    val statements = expVisitor.getExpressionStatements
    val attributes = statements.map(new AssignmentInstrum().assignmentInstrumHelper(_))
    assert(attributes.size > 0)
  }
}
