package visitors

import org.eclipse.jdt.core.dom.{AST, ASTParser, ASTVisitor, CompilationUnit}
import org.scalatest.FunSuite
import parser.visitors.SwitchStatementVisitor

class TestSwitchStatementVisitor extends FunSuite{
  test("StatementVisitor Initialization"){
    val switchStatementVisitor = new SwitchStatementVisitor
    assert(switchStatementVisitor.isInstanceOf[ASTVisitor])
    assert(switchStatementVisitor.getSwitchStatements.length == 0)

  }

  test("SwitchStatementVisitor Visit For in a file without Switch"){

    val sourceCode = """class Test {
                       |    private static void doSomething() {
                       |    }
                       |}""".stripMargin

    val parser = ASTParser.newParser(AST.JLS12)
    parser.setSource(sourceCode.toCharArray)
    parser.setKind(ASTParser.K_COMPILATION_UNIT)
    val cu = parser.createAST(null).asInstanceOf[CompilationUnit]
    val switchStatementVisitor = new SwitchStatementVisitor
    cu.accept(switchStatementVisitor)
    assert(switchStatementVisitor.getSwitchStatements.length == 0)
  }

  test("SwitchStatementVisitor Visit Switch in a file with single switch"){
    val sourceCode = """class Test {
                       |    private static void doSomething() {
                       |       int i = 0;
                       |        switch(i){
                       |        case 0 : System.out.println("hello");
                       |                 break;
                       |        case -1 : System.out.println("hi");
                       |                break;
                       |         default:
                       |                  break;
                       |        }
                       |
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
    val switchStatementVisitor = new SwitchStatementVisitor
    cu.accept(switchStatementVisitor)
    assert(switchStatementVisitor.getSwitchStatements.length == 1)
  }
}

