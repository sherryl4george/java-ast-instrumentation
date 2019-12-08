package Instrumentor

import org.scalatest.FunSuite
import parser.instrumentation.ReturnInstrum
import parser.utils.{ASTParserLocal, FileHelper}
import parser.visitors.ReturnStatementVisitor

class ReturnInstrumTest extends FunSuite{
    test("Return statements instrumented successfully.") {
      val srcPath = getClass.getClassLoader.getResource("testSrc").getPath
      val srcFile = getClass.getClassLoader.getResource("testSrc/MultipleInstrum.java").getPath
      val sourceCode = FileHelper.readFile(srcFile)
      val cu = ASTParserLocal.getCU(srcPath, "", srcFile, "")

      val returnVisitor = new ReturnStatementVisitor
      cu.accept(returnVisitor)
      val statements = returnVisitor.getReturnStatements
      val attributes = statements.map(new ReturnInstrum().returnInstrumHelper(_))
      assert(attributes.size > 0)
    }
 }
