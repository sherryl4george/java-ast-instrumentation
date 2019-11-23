package parser

import java.nio.file.{Files, Paths}

import org.eclipse.jdt.core.JavaCore
import org.eclipse.jdt.core.dom.{AST, ASTParser, CompilationUnit}

import scala.io.Source

class ASTParser {
  private val parser = ASTParser.newParser(AST.JLS12)

  /***
    * Similar to constructor
    * Initializing all required values here
    */
  def this(){
    this()
    this.parser.setKind(ASTParser.K_COMPILATION_UNIT)
    this.parser.setResolveBindings(true)
    val options = JavaCore.getOptions
    parser.setCompilerOptions(options)
    parser.setResolveBindings(true)
    parser.setBindingsRecovery(true)
  }

  def getCU(sources: String, classpath: String, fileName: String): CompilationUnit = {
    parser.setUnitName(fileName)
    parser.setEnvironment(Array[String]("/usr/bin/java/lib/rt.jar"), Array[String](sources), Array[String]("UTF-8"), true)

    val bufferedSource = Source.fromFile(new String(Files.readAllBytes(Paths.get("/media/01D3908E9C0056A0/code/IdeaProjects/cs474.test/src/main/java/test.java"))))
    parser.setSource(bufferedSource.getLines.mkString.toCharArray)
    bufferedSource.close

    parser.createAST(null).asInstanceOf[CompilationUnit]
  }

}
