package visitors

import org.eclipse.jdt.core.dom.{AST, ASTParser, ASTVisitor, CompilationUnit}
import org.scalatest.FunSuite
import parser.visitors.DoStatementVisitor

class TestDoStatementVisitor extends FunSuite{
  test("DoStatementVisitor Initialization"){
    val doStatementVisitor = new DoStatementVisitor
    assert(doStatementVisitor.isInstanceOf[ASTVisitor])
    assert(doStatementVisitor.getDoStatements.length == 0)

  }

  test("DoStatementVisitor Visit Do in a file without DO"){

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
    val doStatementVisitor = new DoStatementVisitor
    cu.accept(doStatementVisitor)
    assert(doStatementVisitor.getDoStatements.length == 0)
  }

  test("DoStatementVisitor Visit Do in a file with single DO"){
    val sourceCode = """class Test {
                       |    private static void doSomething() {
                       |        do{
                       |            System.out.println("test");
                       |        } while (false);
                       |        while(true){
                       |
                       |        }
                       |    }
                       |}""".stripMargin

    val parser = ASTParser.newParser(AST.JLS12)
    parser.setSource(sourceCode.toCharArray)
    parser.setKind(ASTParser.K_COMPILATION_UNIT)
    val cu = parser.createAST(null).asInstanceOf[CompilationUnit]
    val doStatementVisitor = new DoStatementVisitor
    cu.accept(doStatementVisitor)
    assert(doStatementVisitor.getDoStatements.length == 1)
  }

  test("DoStatementVisitor Visit Do in a file with nultiple DO in different methods"){
    val sourceCode = """class Test {
                       |    private static void doSomething() {
                       |        do{
                       |            System.out.println("test");
                       |        } while (false);
                       |        while(true){
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
                       |    }
                       |}""".stripMargin

    val parser = ASTParser.newParser(AST.JLS12)
    parser.setSource(sourceCode.toCharArray)
    parser.setKind(ASTParser.K_COMPILATION_UNIT)
    val cu = parser.createAST(null).asInstanceOf[CompilationUnit]
    val doStatementVisitor = new DoStatementVisitor
    cu.accept(doStatementVisitor)
    assert(doStatementVisitor.getDoStatements.length == 2)
  }
}
