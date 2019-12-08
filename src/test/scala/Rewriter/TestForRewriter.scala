package Rewriter

import org.eclipse.jdt.core.dom.{AST, ASTParser, CompilationUnit}
import org.scalatest.FunSuite
import parser.converters.ForStatementCon
import parser.utils.{ASTParserLocal, FileHelper}

class TestForRewriter extends FunSuite{
  test("For Rewrite test with rewrite.") {
    val srcPath = getClass.getClassLoader.getResource("testSrc").getPath
    val srcFile = getClass.getClassLoader.getResource("testSrc/ForTest.java").getPath
    val sourceCode = FileHelper.readFile(srcFile)
    val cu = ASTParserLocal.getCU(srcPath, "", srcFile, "")
    val forRewriter = new ForStatementCon(cu).startBlockConvert()
    val forCode = FileHelper.getSourceCodeAsString(forRewriter, sourceCode)
    assert(!forCode.equals(sourceCode))
  }

  test("For Rewrite test without re-write.") {
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
    val forRewriter = new ForStatementCon(cu).startBlockConvert()
    val forCode = FileHelper.getSourceCodeAsString(forRewriter, sourceCode)
    assert(forCode.equals(sourceCode))
  }
}
