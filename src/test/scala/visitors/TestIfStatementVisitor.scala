package visitors

import org.eclipse.jdt.core.dom.{AST, ASTParser, ASTVisitor, CompilationUnit}
import org.scalatest.FunSuite
import parser.visitors.IfStatementVisitor

class TestIfStatementVisitor extends FunSuite{
  test("IfStatementVisitor Initialization"){
    val ifStatementVisitor = new IfStatementVisitor
    assert(ifStatementVisitor.isInstanceOf[ASTVisitor])
    assert(ifStatementVisitor.getIfStatements.length == 0)

  }

  test("IfStatementVisitor Visit without if"){

    val sourceCode = """class Test {
                       |    private static void doSomething() {
                       |    }
                       |}""".stripMargin

    val parser = ASTParser.newParser(AST.JLS12)
    parser.setSource(sourceCode.toCharArray)
    parser.setKind(ASTParser.K_COMPILATION_UNIT)
    val cu = parser.createAST(null).asInstanceOf[CompilationUnit]
    val ifStatementVisitor = new IfStatementVisitor
    cu.accept(ifStatementVisitor)
    assert(ifStatementVisitor.getIfStatements.length == 0)
  }

  test("IfStatementVisitor Visit if in a file with single if"){
    val sourceCode = """class Test {
                       |    private static void doSomething() {
                       |        do {
                       |            System.out.println("test");
                       |        } while (false);
                       |        if (true) {
                       |
                       |        }
                       |    }
                       |
                       |}""".stripMargin

    val parser = ASTParser.newParser(AST.JLS12)
    parser.setSource(sourceCode.toCharArray)
    parser.setKind(ASTParser.K_COMPILATION_UNIT)
    val cu = parser.createAST(null).asInstanceOf[CompilationUnit]
    val ifStatementVisitor = new IfStatementVisitor
    cu.accept(ifStatementVisitor)
    assert(ifStatementVisitor.getIfStatements.length == 1)
  }

  test("IfStatementVisitor Visit While in a file with if-else-if ladder"){
    val sourceCode = """class Test {
                       |    private static void doSomething() {
                       |       if(true){
                       |       }
                       |       else if(1 < 2) {
                       |       }
                       |       else if(2 > 3) {
                       |       }
                       |       else{
                       |       }
                       |    }
                       |}""".stripMargin

    val parser = ASTParser.newParser(AST.JLS12)
    parser.setSource(sourceCode.toCharArray)
    parser.setKind(ASTParser.K_COMPILATION_UNIT)
    val cu = parser.createAST(null).asInstanceOf[CompilationUnit]
    val ifStatementVisitor = new IfStatementVisitor
    cu.accept(ifStatementVisitor)
    assert(ifStatementVisitor.getIfStatements.length == 3)
  }
}

