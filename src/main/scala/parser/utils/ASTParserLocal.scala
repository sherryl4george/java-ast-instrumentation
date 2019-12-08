package parser.utils

import com.typesafe.scalalogging.LazyLogging
import org.eclipse.jdt.core.JavaCore
import org.eclipse.jdt.core.dom.{AST, ASTParser, CompilationUnit}

/**
The AST Parser class to get a compilation unit, given a source.
 **/
object ASTParserLocal extends LazyLogging{
    /**
    This method retrieves the compilation unit from the AST built from the given source file.
   **/
    def getCU(sourcePath: String, classpath: String, fileName: String, sourceCode: String): CompilationUnit = {
        logger.debug("Retrieving compilation unit from the source")
    val parser = ASTParser.newParser(AST.JLS12)
    parser.setKind(ASTParser.K_COMPILATION_UNIT)
    parser.setResolveBindings(true)
    val options = JavaCore.getOptions
    parser.setCompilerOptions(options)
    parser.setResolveBindings(true)
    parser.setBindingsRecovery(true)
    parser.setUnitName(fileName)

      //Set source from source file, if the passed testSrc string is empty.
    if (sourceCode.length == 0)
      parser.setSource(FileHelper.readFile(fileName).toCharArray)
    else
      parser.setSource(sourceCode.toCharArray)
    parser.setEnvironment(Array[String]("/usr/bin/java/lib/rt.jar"), Array[String](sourcePath), Array[String]("UTF-8"), true)

      //Retrieves the AST and returns a compilation unit.
    parser.createAST(null).asInstanceOf[CompilationUnit]
  }
}

