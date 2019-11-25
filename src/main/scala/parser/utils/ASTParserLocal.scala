package parser.utils

import org.eclipse.jdt.core.JavaCore
import org.eclipse.jdt.core.dom.{AST, ASTParser, CompilationUnit}

object ASTParserLocal {

  def getParser(sourcePath: String, classpath: String, fileName: String): ASTParser = {
    val parser = ASTParser.newParser(AST.JLS12)
    parser.setKind(ASTParser.K_COMPILATION_UNIT)
    parser.setResolveBindings(true)
    val options = JavaCore.getOptions
    parser.setCompilerOptions(options)
    parser.setResolveBindings(true)
    parser.setBindingsRecovery(true)
    parser.setUnitName(fileName)
    // Catch and do something about exception
    parser.setEnvironment(Array[String]("/usr/bin/java/lib/rt.jar"), Array[String](sourcePath), Array[String]("UTF-8"), true)
        parser
  }

  def getCU(parser: ASTParser, code:String):CompilationUnit = {
    parser.setSource(code.toCharArray)
    parser.createAST(null).asInstanceOf[CompilationUnit]
  }
}
