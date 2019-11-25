package parser.utils

import org.eclipse.jdt.core.{ICompilationUnit, JavaCore}
import org.eclipse.jdt.core.dom.{AST, ASTParser, CompilationUnit}

object ASTParserLocal {

  def getParser(sourcePath: String, classpath: String, fileName: String, sourceCode: String): CompilationUnit = {
    val parser = ASTParser.newParser(AST.JLS12)
    parser.setKind(ASTParser.K_COMPILATION_UNIT)
    parser.setResolveBindings(true)
    val options = JavaCore.getOptions
    parser.setCompilerOptions(options)
    parser.setResolveBindings(true)
    parser.setBindingsRecovery(true)
    parser.setUnitName(fileName)
    // Catch and do something about exception
    if(sourceCode.length==0)
      parser.setSource(FileWriter.readFile(fileName).toCharArray)
    else
      parser.setSource(sourceCode.toCharArray)
    parser.setEnvironment(Array[String]("/usr/bin/java/lib/rt.jar"), Array[String](sourcePath), Array[String]("UTF-8"), true)
    parser.createAST(null).asInstanceOf[CompilationUnit] //.getJavaElement.asInstanceOf[ICompilationUnit]
  }

//  def getCU(parser: ASTParser, code:String):CompilationUnit = {
//    val u = parser.createAST(null).asInstanceOf[CompilationUnit].getJavaElement.asInstanceOf[ICompilationUnit]
//    u.getBuffer.setContents(code)
//    parser.setSource(u)
//  }

  import org.eclipse.jdt.core.dom.AST
  import org.eclipse.jdt.core.dom.ASTParser

  def parse(unit: ICompilationUnit): CompilationUnit = {
//    val parser = ASTParser.newParser(AST.JLS12)
//    parser.setKind(ASTParser.K_COMPILATION_UNIT)
//    parser.setSource(unit)
//    parser.setResolveBindings(true)
//    parser.createAST(null).asInstanceOf[CompilationUnit] // parse
val parser = ASTParser.newParser(AST.JLS12)
    parser.setKind(ASTParser.K_COMPILATION_UNIT)
    parser.setResolveBindings(true)
    val options = JavaCore.getOptions
    parser.setCompilerOptions(options)
    parser.setResolveBindings(true)
    parser.setBindingsRecovery(true)
    // Catch and do something about exception
    parser.setSource(unit)
//    parser.setEnvironment(Array[String]("/usr/bin/java/lib/rt.jar"), Array[String](sourcePath), Array[String]("UTF-8"), true)
    parser.createAST(null).asInstanceOf[CompilationUnit] //.getJavaElement.asInstanceOf[ICompilationUnit]

  }
}
