package visitors

import org.eclipse.jdt.core.dom.{AST, ASTParser, ASTVisitor, CompilationUnit}
import org.scalatest.FunSuite
import parser.visitors.ReturnStatementVisitor

class TestReturnStatementVisitor extends FunSuite{
  test("ReturnStatementVisitor Initialization"){
    val returnStatementVisitor = new ReturnStatementVisitor
    assert(returnStatementVisitor.isInstanceOf[ASTVisitor])
    assert(returnStatementVisitor.getReturnStatements.length == 0)

  }

  test("Return statement with no returns"){
    val sourceCode = """class Test {
                       |    private static void doSomething() {
                       |      int x = 0,i = 1 ,j = 2;
                       |    }
                       |}""".stripMargin

    val parser = ASTParser.newParser(AST.JLS12)
    parser.setSource(sourceCode.toCharArray)
    parser.setKind(ASTParser.K_COMPILATION_UNIT)
    val cu = parser.createAST(null).asInstanceOf[CompilationUnit]
    val returnstatementVisitor = new ReturnStatementVisitor
    cu.accept(returnstatementVisitor)
    assert(returnstatementVisitor.getReturnStatements.length == 0)
  }

  test("ReturnStatement Visit in a file with a single return"){
    val sourceCode = """class Test {
                       |    private static int doSomething() {
                       |        int x = 0,i = 1 ,j = 2;
                       |        x = i + j;
                       |        return x;
                       |    }
                       |}""".stripMargin

    val parser = ASTParser.newParser(AST.JLS12)
    parser.setSource(sourceCode.toCharArray)
    parser.setKind(ASTParser.K_COMPILATION_UNIT)
    val cu = parser.createAST(null).asInstanceOf[CompilationUnit]
    val returnStatementVisitor = new ReturnStatementVisitor
    cu.accept(returnStatementVisitor)
    assert(returnStatementVisitor.getReturnStatements.length == 1)
  }

  test("Return Statement visit in a file with multiple types of return statements."){
    val sourceCode = """class Test {
                       |    private static int doSomething() {
                       |      int x = 0, i = 1, j = 2;
                       |      x = i + j;
                       |      return doSomethingAgain(x).toInteger();
                       |    }
                       |    private static String doSomethingAgain(int x) {
                       |        System.out.println(x);
                       |        return x.toString();
                       |    }
                       |}""".stripMargin

    val parser = ASTParser.newParser(AST.JLS12)
    parser.setSource(sourceCode.toCharArray)
    parser.setKind(ASTParser.K_COMPILATION_UNIT)
    val cu = parser.createAST(null).asInstanceOf[CompilationUnit]
    val returnStatementVisitor = new ReturnStatementVisitor
    cu.accept(returnStatementVisitor)
    assert(returnStatementVisitor.getReturnStatements.length == 2)
  }
}

