package visitors

import org.eclipse.jdt.core.dom.{AST, ASTParser, ASTVisitor, CompilationUnit}
import org.scalatest.FunSuite
import parser.visitors.VDStatementVisitor

class TestVDSStatementVisitor extends FunSuite{
  test("VDSStatementVisitor Initialization"){
    val vdsStatementVisitor = new VDStatementVisitor
    assert(vdsStatementVisitor.isInstanceOf[ASTVisitor])
    assert(vdsStatementVisitor.getVariableDeclarationStatements.length == 0)

  }

  test("VDSStatementVisitor Visit VDS in a file without VDS"){
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
    val vdsStatementVisitor = new VDStatementVisitor
    cu.accept(vdsStatementVisitor)
    assert(vdsStatementVisitor.getVariableDeclarationStatements.length == 0)
  }

  test("DoStatementVisitor Visit Do in a file with single DO"){
    val sourceCode = """class Test {
                       |    private static void doSomething() {
                       |    int i = 0, j = 5, k = 7;
                       |    }
                       |}""".stripMargin

    val parser = ASTParser.newParser(AST.JLS12)
    parser.setSource(sourceCode.toCharArray)
    parser.setKind(ASTParser.K_COMPILATION_UNIT)
    val cu = parser.createAST(null).asInstanceOf[CompilationUnit]
    val vdsStatementVisitor = new VDStatementVisitor
    cu.accept(vdsStatementVisitor)
    assert(vdsStatementVisitor.getVariableDeclarationStatements.length == 1)
  }

  test("VDSStatementVisitor Visit  in a file with multiple VDS in different methods"){
    val sourceCode = """class Test {
                       |    private static void doSomething() {
                       |          int i = 0;
                       |          int j = i + 2;
                       |    }
                       |    private static void doSomethingAgain() {
                       |        int k = 5 + 7;
                       |        int l = k;
                       |        }
                       |    }
                       |}""".stripMargin

    val parser = ASTParser.newParser(AST.JLS12)
    parser.setSource(sourceCode.toCharArray)
    parser.setKind(ASTParser.K_COMPILATION_UNIT)
    val cu = parser.createAST(null).asInstanceOf[CompilationUnit]
    val vdsStatementVisitor = new VDStatementVisitor
    cu.accept(vdsStatementVisitor)
    assert(vdsStatementVisitor.getVariableDeclarationStatements.length == 4)
  }
}
