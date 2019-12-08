package visitors

import org.eclipse.jdt.core.dom.{AST, ASTParser, ASTVisitor, CompilationUnit}
import org.scalatest.FunSuite
import parser.visitors.EnhancedForVisitor

class TestEnhancedForVisitor extends FunSuite{
  test("EnhancedForVisitor Initialization"){
    val enchancedForStatementVisitor = new EnhancedForVisitor
    assert(enchancedForStatementVisitor.isInstanceOf[ASTVisitor])
    assert(enchancedForStatementVisitor.getForStatements.length == 0)

  }

  test("EnhancedForVisitor Visit forEach in a file without forEach"){

    val sourceCode = """class Test {
                       |    private static void doSomething() {
                       |    }
                       |}""".stripMargin

    val parser = ASTParser.newParser(AST.JLS12)
    parser.setSource(sourceCode.toCharArray)
    parser.setKind(ASTParser.K_COMPILATION_UNIT)
    val cu = parser.createAST(null).asInstanceOf[CompilationUnit]
    val forStatementVisitor = new EnhancedForVisitor
    cu.accept(forStatementVisitor)
    assert(forStatementVisitor.getForStatements.length == 0)
  }

  test("EnhancedForVisitor Visit forEach in a file with single forEach"){
    val sourceCode = """class Test {
                       |    private static void doSomething() {
                       |        do {
                       |            System.out.println("test");
                       |        } while (false);
                       |        for(char c: "string".toCharArray()) {
                       |        }
                       |    }
                       |
                       |}""".stripMargin

    val parser = ASTParser.newParser(AST.JLS12)
    parser.setSource(sourceCode.toCharArray)
    parser.setKind(ASTParser.K_COMPILATION_UNIT)
    val cu = parser.createAST(null).asInstanceOf[CompilationUnit]
    val forStatementVisitor = new EnhancedForVisitor
    cu.accept(forStatementVisitor)
    assert(forStatementVisitor.getForStatements.length == 1)
  }

  test("EnhancedForVisitor Visit forEach in a file with multiple forEach in different methods"){
    val sourceCode = """class Test {
                       |    private static void doSomething() {
                       |        do {
                       |            System.out.println("test");
                       |        } while (false);
                       |        for(char c: "string".toCharArray()) {
                       |        }
                       |    }
                       |    private static void doSomethingAgain() {
                       |        for (int i = 0; i < 10; i++) {
                       |
                       |        }
                       |        for(char c: "anotherstring".toCharArray()) {
                       |        }
                       |    }
                       |}""".stripMargin

    val parser = ASTParser.newParser(AST.JLS12)
    parser.setSource(sourceCode.toCharArray)
    parser.setKind(ASTParser.K_COMPILATION_UNIT)
    val cu = parser.createAST(null).asInstanceOf[CompilationUnit]
    val forStatementVisitor = new EnhancedForVisitor
    cu.accept(forStatementVisitor)
    assert(forStatementVisitor.getForStatements.length == 2)
  }
}

