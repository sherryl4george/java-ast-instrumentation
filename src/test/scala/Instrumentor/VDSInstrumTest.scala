package Instrumentor

import org.scalatest.FunSuite
import parser.instrumentation.VDSInstrum
import parser.utils.{ASTParserLocal, FileHelper}
import parser.visitors.VDStatementVisitor

class VDSInstrumTest extends FunSuite{
    test("VDS statements instrumented successfully.") {
      val srcPath = getClass.getClassLoader.getResource("testSrc").getPath
      val srcFile = getClass.getClassLoader.getResource("testSrc/VDSInstrum1.java").getPath
      val sourceCode = FileHelper.readFile(srcFile)
      val cu = ASTParserLocal.getCU(srcPath, "", srcFile, "")

      val vdsVisitor = new VDStatementVisitor
      cu.accept(vdsVisitor)
      val statements = vdsVisitor.getVariableDeclarationStatements
      val attributes = statements.map(new VDSInstrum().varDFragmentInstrumHelper(_))
      assert(attributes.size > 0)
    }

  test("VDS statements with initializer instrumented successfully.") {
    val srcPath = getClass.getClassLoader.getResource("testSrc").getPath
    val srcFile = getClass.getClassLoader.getResource("testSrc/VDSInstrum1.java").getPath
    val sourceCode = FileHelper.readFile(srcFile)
    val cu = ASTParserLocal.getCU(srcPath, "", srcFile, "")

    val vdsVisitor = new VDStatementVisitor
    cu.accept(vdsVisitor)
    val statements = vdsVisitor.getVariableDeclarationStatements
    val attributes = statements.map(new VDSInstrum().varDFragmentInstrumHelper(_))
    val result = attributes.flatMap(_.filter(_._2))
    assert(result.size > 0)
  }

  test("VDS statements without initializer instrumented successfully.") {
    val srcPath = getClass.getClassLoader.getResource("testSrc").getPath
    val srcFile = getClass.getClassLoader.getResource("testSrc/VDSInstrum2.java").getPath
    val sourceCode = FileHelper.readFile(srcFile)
    val cu = ASTParserLocal.getCU(srcPath, "", srcFile, "")

    val vdsVisitor = new VDStatementVisitor
    cu.accept(vdsVisitor)
    val statements = vdsVisitor.getVariableDeclarationStatements
    val attributes = statements.map(new VDSInstrum().varDFragmentInstrumHelper(_))
    val result = attributes.flatMap(_.filter(_._2))
    assert(result.size == 0)

  }



}
