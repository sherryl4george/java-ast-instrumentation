package Instrumentor

import org.scalatest.FunSuite
import parser.instrumentation.MethodDeclarationInstrum
import parser.utils.{ASTParserLocal, FileHelper}
import parser.visitors.MethodDeclarationVisitor

class MethodDeclInstrumTest extends FunSuite{
    test("Method Decl statements instrumented successfully.") {
      val srcPath = getClass.getClassLoader.getResource("testSrc").getPath
      val srcFile = getClass.getClassLoader.getResource("testSrc/MultipleInstrum.java").getPath
      val sourceCode = FileHelper.readFile(srcFile)
      val cu = ASTParserLocal.getCU(srcPath, "", srcFile, "")

      val methodVisitor = new MethodDeclarationVisitor
      cu.accept(methodVisitor)
      val statements = methodVisitor.getMethodDeclarations
      val attributes = statements.map(new MethodDeclarationInstrum().methodDeclarationInstrumHelper(_))
      assert(attributes.size == 2)
    }
 }
