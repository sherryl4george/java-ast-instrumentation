package Rewriter

import org.eclipse.jdt.core.dom.{AST, ASTParser, CompilationUnit}
import org.scalatest.FunSuite
import parser.converters.DoStatementCon
import parser.utils.{ASTParserLocal, FileHelper}

class TestDoWhileRewriter extends FunSuite {
  test("Do-while Rewrite test without rewrite.") {
    val sourceCode =
      """class Test {
        |    private static void doSomething() {
        |      int x = 0, i = 1, j = 2;
        |      x = i + j;
        |      doSomethingAgain(x);
        |    }
        |    private static void doSomethingAgain(int x) {
        |      System.out.println(x);
        |    }
        |}""".stripMargin

    val parser = ASTParser.newParser(AST.JLS12)
    parser.setSource(sourceCode.toCharArray)
    parser.setKind(ASTParser.K_COMPILATION_UNIT)
    val cu = parser.createAST(null).asInstanceOf[CompilationUnit]
    val doRewriter = new DoStatementCon(cu).startBlockConvert()
    val doCode = FileHelper.getSourceCodeAsString(doRewriter, sourceCode)
    assert(doCode.equals(sourceCode))
  }

  test("Do Rewrite test with rewrite.") {
    val srcPath = getClass.getClassLoader.getResource("testSrc").getPath
    val srcFile = getClass.getClassLoader.getResource("testSrc/DoTest.java").getPath
    val sourceCode = FileHelper.readFile(srcFile)
    val cu = ASTParserLocal.getCU(srcPath, "", srcFile, "")
    val doRewriter = new DoStatementCon(cu).startBlockConvert()
    val doCode = FileHelper.getSourceCodeAsString(doRewriter, sourceCode)
    assert(!doCode.equals(sourceCode))
  }
}
