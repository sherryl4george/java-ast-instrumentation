package parser.utils

import java.io.{File, IOException}

import org.apache.commons.io.FileUtils.{readFileToString, writeStringToFile}
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite
import org.eclipse.jface.text.{BadLocationException, Document}

object FileWriter {
  def writeFile(rewriter: ASTRewrite, fileName: String, output:String) = {
    var document2 = new Document(readFileToString(new File(fileName)))
    val edits = rewriter.rewriteAST(document2, null)
    try
      edits.apply(document2)
    catch {
      case e: BadLocationException =>
        e.printStackTrace()
    }
    val fileValue = document2.get
    try {
      println(fileValue)
      writeStringToFile(new File(output), fileValue)
    }
    catch {
      case e: IOException =>
        e.printStackTrace()
    }
  }

}
