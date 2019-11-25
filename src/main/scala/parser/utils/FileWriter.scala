package parser.utils

import java.io.{File, IOException}

import org.apache.commons.io.FileUtils.{readFileToString, writeStringToFile}
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite
import org.eclipse.jface.text.{BadLocationException, Document}

object FileWriter {
  def writeFile(sourceCode: String, outputFile:String) = {
    try {
      println(sourceCode)
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

}
