package parser.utils

import java.io.{File, IOException}

import com.typesafe.scalalogging.LazyLogging
import org.apache.commons.io.FileUtils.{readFileToString, writeStringToFile}
import org.apache.commons.io.filefilter.TrueFileFilter
import org.apache.commons.io.{FileUtils, FilenameUtils}
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite
import org.eclipse.jface.text.{BadLocationException, Document}

import scala.jdk.CollectionConverters._

/**
The util class to handle file operations.
 */
object FileHelper extends LazyLogging {

  /**
   * Writes a source code string to a file.
   * @param sourceCode
   * @param outputFile
   */
  def writeFile(sourceCode: String, outputFile:String) = {
    try {
      writeStringToFile(new File(outputFile), sourceCode)
      logger.debug("Code written to file => " + outputFile)
    }
    catch {
      case e: IOException => {
        e.printStackTrace()
        logger.error("Error occured when writing to file =>" + e.getMessage)
      }
    }
  }

  /**
   * Apply the rewritten AST to the old source code and get the modified source as a string.
   * @param rewriter
   * @param oldSourceCode
   * @return
   */
  def getSourceCodeAsString(rewriter: ASTRewrite, oldSourceCode: String): String ={
    var document = new Document(oldSourceCode)
    val edits = rewriter.rewriteAST(document, null)
    try
      edits.apply(document)
    catch {
      case e: BadLocationException => {
        e.printStackTrace()
        logger.error("Error reading source code as string => " + e.getMessage)
      }
    }
    document.get
  }

  /**
   * Reads a file and returns the file contents as a string.
   * @param fileName
   * @return
   */
  def readFile(fileName: String): String = readFileToString(new File(fileName))

  /**
   * Filters a list of files by extension.
   * @param source
   * @param extension
   * @return
   */
  def getFilesByExtension(source: String, extension: String) : List[File] = {
    logger.info("Getting files from " + source + "with extension => " + extension)
    FileUtils.listFiles(new File(source), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE).asScala.toList.filter(file=>{
      FilenameUtils.getExtension(file.getAbsolutePath).equals(extension)
    })
  }

}
