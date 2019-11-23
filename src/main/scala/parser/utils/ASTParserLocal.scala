package parser.utils

import java.io.File

import org.apache.commons.io.FileUtils.readFileToString
import org.eclipse.jdt.core.JavaCore
import org.eclipse.jdt.core.dom.{AST, ASTParser, CompilationUnit}

class ASTParserLocal {
  private val parser = ASTParser.newParser(AST.JLS12)
  //
  //  /***
  //    * Similar to constructor
  //    * Initializing all required values here
  //    */
  //  def this(){
  //    this()
  //    this.parser.setKind(ASTParser.K_COMPILATION_UNIT)
  //    this.parser.setResolveBindings(true)
  //    val options = JavaCore.getOptions
  //    parser.setCompilerOptions(options)
  //    parser.setResolveBindings(true)
  //    parser.setBindingsRecovery(true)
  //  }

  def getCU(sources: String, classpath: String, fileName: String): CompilationUnit = {
    this.parser.setKind(ASTParser.K_COMPILATION_UNIT)
    this.parser.setResolveBindings(true)
    val options = JavaCore.getOptions
    parser.setCompilerOptions(options)
    parser.setResolveBindings(true)
    parser.setBindingsRecovery(true)
    parser.setUnitName(fileName)
    // Catch and do something about exception
    parser.setEnvironment(Array[String]("/usr/bin/java/lib/rt.jar"), Array[String](sources), Array[String]("UTF-8"), true)

    parser.setSource(readFileToString(new File(fileName)).toCharArray)
//    val bufferedSource = Source.fromFile(new String(Files.readAllBytes(Paths.get(fileName))))
//    parser.setSource(bufferedSource.getLines.mkString.toCharArray)
//    bufferedSource.close

    parser.createAST(null).asInstanceOf[CompilationUnit]
  }

}
