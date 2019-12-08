package visitors

import org.eclipse.jdt.core.dom.{AST, ASTParser, ASTVisitor, CompilationUnit}
import org.scalatest.FunSuite
import parser.visitors.MethodDeclarationVisitor

class TestMethodDeclarationVisitor extends FunSuite{
  test("MethodStatementVisitor Initialization"){
    val methDeclVisitor = new MethodDeclarationVisitor
    assert(methDeclVisitor.isInstanceOf[ASTVisitor])
    assert(methDeclVisitor.getMethodDeclarations.length == 0)
  }

  test("MethodStatementVisitor Visit without method declarations"){

    val sourceCode = """class Test {

                       |}""".stripMargin

    val parser = ASTParser.newParser(AST.JLS12)
    parser.setSource(sourceCode.toCharArray)
    parser.setKind(ASTParser.K_COMPILATION_UNIT)
    val cu = parser.createAST(null).asInstanceOf[CompilationUnit]
    val methDeclVisitor = new MethodDeclarationVisitor
    cu.accept(methDeclVisitor)
    assert(methDeclVisitor.getMethodDeclarations.length == 0)
  }

  test("MethodDeclaration Visit  in a file with single method declaration main"){
    val sourceCode = """class Test {
                       |    public static void main(String args[]) {
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
    val methDeclVisitor = new MethodDeclarationVisitor
    cu.accept(methDeclVisitor)
    assert(methDeclVisitor.getMethodDeclarations.length == 1)
  }

  test("Method Declaration Visitor with multiple methods"){
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
    val methDeclVisitor = new MethodDeclarationVisitor
    cu.accept(methDeclVisitor)
    assert(methDeclVisitor.getMethodDeclarations.length == 2)
  }
}

