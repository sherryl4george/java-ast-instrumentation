package visitors

import org.eclipse.jdt.core.dom.{AST, ASTParser, ASTVisitor, CompilationUnit}
import org.scalatest.FunSuite
import parser.visitors.ForStatementVisitor

class TestForStatementVisitor extends FunSuite{
  test("ForStatementVisitor Initialization"){
    val forStatementVisitor = new ForStatementVisitor
    assert(forStatementVisitor.isInstanceOf[ASTVisitor])
    assert(forStatementVisitor.getForStatements.length == 0)

  }

  test("ForStatementVisitor Visit For in a file without FOR"){

    val sourceCode = """class Test {
                       |    private static void doSomething() {
                       |        while(true){
                       |
                       |        }
                       |    }
                       |}""".stripMargin

    val parser = ASTParser.newParser(AST.JLS12)
    parser.setSource(sourceCode.toCharArray)
    parser.setKind(ASTParser.K_COMPILATION_UNIT)
    val cu = parser.createAST(null).asInstanceOf[CompilationUnit]
    val forStatementVisitor = new ForStatementVisitor
    cu.accept(forStatementVisitor)
    assert(forStatementVisitor.getForStatements.length == 0)
  }

  test("ForStatementVisitor Visit For in a file with single FOR"){
    val sourceCode = """class Test {
                       |    private static void doSomething() {
                       |        do {
                       |            System.out.println("test");
                       |        } while (false);
                       |        for (int i = 0; i < 10; i++) {
                       |
                       |        }
                       |    }
                       |
                       |}""".stripMargin

    val parser = ASTParser.newParser(AST.JLS12)
    parser.setSource(sourceCode.toCharArray)
    parser.setKind(ASTParser.K_COMPILATION_UNIT)
    val cu = parser.createAST(null).asInstanceOf[CompilationUnit]
    val forStatementVisitor = new ForStatementVisitor
    cu.accept(forStatementVisitor)
    assert(forStatementVisitor.getForStatements.length == 1)
  }

  test("ForStatementVisitor Visit For in a file with multiple FOR in different methods"){
    val sourceCode = """class Test {
                       |    private static void doSomething() {
                       |        do {
                       |            System.out.println("test");
                       |        } while (false);
                       |        for (int i = 0; i < 10; i++) {
                       |
                       |        }
                       |    }
                       |
                       |    private static void doSomethingAgain() {
                       |        do{
                       |            System.out.println("test");
                       |        } while (false);
                       |        for (int i = 0; i < 10; i++) {
                       |
                       |        }
                       |    }
                       |}""".stripMargin

    val parser = ASTParser.newParser(AST.JLS12)
    parser.setSource(sourceCode.toCharArray)
    parser.setKind(ASTParser.K_COMPILATION_UNIT)
    val cu = parser.createAST(null).asInstanceOf[CompilationUnit]
    val forStatementVisitor = new ForStatementVisitor
    cu.accept(forStatementVisitor)
    assert(forStatementVisitor.getForStatements.length == 2)
  }
}

