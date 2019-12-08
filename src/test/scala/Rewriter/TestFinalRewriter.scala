package Rewriter

import org.eclipse.jdt.core.dom.{AST, ASTParser, CompilationUnit}
import org.scalatest.FunSuite
import parser.converters.FinalConverter
import parser.utils.{ASTParserLocal, FileHelper}

class TestFinalRewriter extends FunSuite{
  test("Rewrite test with imports and without main.") {
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
    val finalRewriter = new FinalConverter(cu).startInstrum()
    val finalCode = FileHelper.getSourceCodeAsString(finalRewriter, sourceCode)
    assert(!finalCode.equals(sourceCode))
  }

  test("Rewrite test with main") {
    val srcPath = getClass.getClassLoader.getResource("testSrc").getPath
    val srcFile = getClass.getClassLoader.getResource("testSrc/MainTest.java").getPath
    val sourceCode = FileHelper.readFile(srcFile)
    val cu = ASTParserLocal.getCU(srcPath, "", srcFile, "")
    val finalRewriter = new FinalConverter(cu).startInstrum()
    val finalCode = FileHelper.getSourceCodeAsString(finalRewriter, sourceCode)
    assert(!finalCode.equals(sourceCode))
  }
}
