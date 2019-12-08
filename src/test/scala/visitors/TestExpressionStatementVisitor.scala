package visitors

import org.eclipse.jdt.core.dom.{AST, ASTParser, ASTVisitor, CompilationUnit}
import org.scalatest.FunSuite
import parser.visitors.ExpressionStatementVisitor

class TestExpressionStatementVisitor extends FunSuite{
  test("ExpressionStatementVisitor Initialization"){
    val expressionStatementVisitor = new ExpressionStatementVisitor
    assert(expressionStatementVisitor.isInstanceOf[ASTVisitor])
    assert(expressionStatementVisitor.getExpressionStatements.length == 0)

  }

  test("Expression statement with no expressions"){
    val sourceCode = """class Test {
                       |    private static void doSomething() {
                       |      int x = 0,i = 1 ,j = 2;
                       |    }
                       |}""".stripMargin

    val parser = ASTParser.newParser(AST.JLS12)
    parser.setSource(sourceCode.toCharArray)
    parser.setKind(ASTParser.K_COMPILATION_UNIT)
    val cu = parser.createAST(null).asInstanceOf[CompilationUnit]
    val expressionStatementVisitor = new ExpressionStatementVisitor
    cu.accept(expressionStatementVisitor)
    assert(expressionStatementVisitor.getExpressionStatements.length == 0)
  }

  test("ExpressionStatement Visit in a file with a single assignment"){
    val sourceCode = """class Test {
                       |    private static void doSomething() {
                       |        int x = 0,i = 1 ,j = 2;
                       |        x = i + j;
                       |    }
                       |}""".stripMargin

    val parser = ASTParser.newParser(AST.JLS12)
    parser.setSource(sourceCode.toCharArray)
    parser.setKind(ASTParser.K_COMPILATION_UNIT)
    val cu = parser.createAST(null).asInstanceOf[CompilationUnit]
    val expressionStatementVisitor = new ExpressionStatementVisitor
    cu.accept(expressionStatementVisitor)
    assert(expressionStatementVisitor.getExpressionStatements.length == 1)
  }

  test("Expression Statement visit in a file with multiple types of expression statements."){
    val sourceCode = """class Test {
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
    val expressionStatementVisitor = new ExpressionStatementVisitor
    cu.accept(expressionStatementVisitor)
    assert(expressionStatementVisitor.getExpressionStatements.length == 3)
  }
}

