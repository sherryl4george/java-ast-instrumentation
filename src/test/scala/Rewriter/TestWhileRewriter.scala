package Rewriter

import org.eclipse.jdt.core.dom.{AST, ASTParser, CompilationUnit}
import org.scalatest.FunSuite
import parser.converters.WhileStatementCon
import parser.utils.{ASTParserLocal, FileHelper}

class TestWhileRewriter extends FunSuite {
  test("While Rewrite test without while.") {
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
    val whileRewriter = new WhileStatementCon(cu).startBlockConvert()
    val whileCode = FileHelper.getSourceCodeAsString(whileRewriter, sourceCode)
    assert(whileCode.equals(sourceCode))
  }

  test("While Rewrite test with rewrite.") {
    val srcPath = getClass.getClassLoader.getResource("testSrc").getPath
    val srcFile = getClass.getClassLoader.getResource("testSrc/WhileTest.java").getPath
    val sourceCode = FileHelper.readFile(srcFile)
    val cu = ASTParserLocal.getCU(srcPath, "", srcFile, "")
    val whileRewriter = new WhileStatementCon(cu).startBlockConvert()
    val whileCode = FileHelper.getSourceCodeAsString(whileRewriter, sourceCode)
    assert(!whileCode.equals(sourceCode))
  }
}
