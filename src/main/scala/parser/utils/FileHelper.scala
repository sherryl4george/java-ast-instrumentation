package parser.utils

import java.io.{File, IOException}

import org.apache.commons.io.FileUtils.{readFileToString, writeStringToFile}
import org.apache.commons.io.filefilter.TrueFileFilter
import org.apache.commons.io.{FileUtils, FilenameUtils}
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite
import org.eclipse.jface.text.{BadLocationException, Document}
import scala.jdk.CollectionConverters._

object FileHelper {
  def writeFile(sourceCode: String, outputFile:String) = {
    try {
      writeStringToFile(new File(outputFile), sourceCode)
    }
    catch {
      case e: IOException =>
        e.printStackTrace()
    }
  }

  def getSourceCodeAsString(rewriter: ASTRewrite, oldSourceCode: String): String ={
    var document = new Document(oldSourceCode)
    val edits = rewriter.rewriteAST(document, null)
    try
      edits.apply(document)
    catch {
      case e: BadLocationException =>
        e.printStackTrace()
    }
    document.get
  }

  def readFile(fileName: String): String = readFileToString(new File(fileName))

  def getJavaFiles(source: String) : List[File] = {
    FileUtils.listFiles(new File(source), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE).asScala.toList.filter(file=>{
      FilenameUtils.getExtension(file.getAbsolutePath).equals("java")
    })
  }

}
