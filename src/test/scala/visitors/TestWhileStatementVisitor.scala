package visitors

import org.eclipse.jdt.core.dom.{AST, ASTParser, ASTVisitor, CompilationUnit}
import org.scalatest.FunSuite
import parser.visitors.WhileStatementVisitor

class TestWhileStatementVisitor extends FunSuite{
  test("WhileStatementVisitor Initialization"){
    val forStatementVisitor = new WhileStatementVisitor
    assert(forStatementVisitor.isInstanceOf[ASTVisitor])
    assert(forStatementVisitor.getWhileStatements.length == 0)

  }

  test("WhileStatementVisitor Visit For in a file without FOR"){

    val sourceCode = """class Test {
                       |    private static void doSomething() {
                       |    }
                       |}""".stripMargin

    val parser = ASTParser.newParser(AST.JLS12)
    parser.setSource(sourceCode.toCharArray)
    parser.setKind(ASTParser.K_COMPILATION_UNIT)
    val cu = parser.createAST(null).asInstanceOf[CompilationUnit]
    val whileStatementVisitor = new WhileStatementVisitor
    cu.accept(whileStatementVisitor)
    assert(whileStatementVisitor.getWhileStatements.length == 0)
  }

  test("WhileStatementVisitor Visit While in a file with single While"){
    val sourceCode = """class Test {
                       |    private static void doSomething() {
                       |        do {
                       |            System.out.println("test");
                       |        } while (false);
                       |        while (true) {
                       |
                       |        }
                       |    }
                       |
                       |}""".stripMargin

    val parser = ASTParser.newParser(AST.JLS12)
    parser.setSource(sourceCode.toCharArray)
    parser.setKind(ASTParser.K_COMPILATION_UNIT)
    val cu = parser.createAST(null).asInstanceOf[CompilationUnit]
    val whileStatementVisitor = new WhileStatementVisitor
    cu.accept(whileStatementVisitor)
    assert(whileStatementVisitor.getWhileStatements.length == 1)
  }

  test("WhileStatementVisitor Visit While in a file with multiple While in different methods"){
    val sourceCode = """class Test {
                       |    private static void doSomething() {
                       |        do {
                       |            System.out.println("test");
                       |        } while (false);
                       |        while (true) {
                       |
                       |        }
                       |    }
                       |    private static void doSomethingAgain() {
                       |        do{
                       |            System.out.println("test");
                       |        } while (false);
                       |        while(true){
                       |
                       |        }
                       |        for (int i = 0; i < 10; i++) {
                       |
                       |        }
                       |    }
                       |}""".stripMargin

    val parser = ASTParser.newParser(AST.JLS12)
    parser.setSource(sourceCode.toCharArray)
    parser.setKind(ASTParser.K_COMPILATION_UNIT)
    val cu = parser.createAST(null).asInstanceOf[CompilationUnit]
    val whileStatementVisitor = new WhileStatementVisitor
    cu.accept(whileStatementVisitor)
    assert(whileStatementVisitor.getWhileStatements.length == 2)
  }
}

