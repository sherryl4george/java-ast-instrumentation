package Rewriter

import org.eclipse.jdt.core.dom.{AST, ASTParser, CompilationUnit}
import org.scalatest.FunSuite
import parser.converters.{ DoStatementCon}
import parser.utils.FileHelper

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
    val sourceCode =
      """class Test {
        |    private static int doSomething() {
        |      int x = 5;
        |      return x;
        |    }
        |
        |    private static void doSomethingAgain() {
        |    int y = 0;
        |     do {
        |       y++;
        |      } while(doSomething() > 5);
        |    }
        |}""".stripMargin

    val parser = ASTParser.newParser(AST.JLS12)
    parser.setSource(sourceCode.toCharArray)
    parser.setKind(ASTParser.K_COMPILATION_UNIT)
    val cu = parser.createAST(null).asInstanceOf[CompilationUnit]
    val doRewriter = new DoStatementCon(cu).startBlockConvert()
    val doCode = FileHelper.getSourceCodeAsString(doRewriter, sourceCode)
    assert(!doCode.equals(sourceCode))
  }
}
