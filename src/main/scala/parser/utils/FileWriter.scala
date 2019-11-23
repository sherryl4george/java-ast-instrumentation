package parser.utils

import java.io.{File, IOException}

import org.apache.commons.io.FileUtils.{readFileToString, writeStringToFile}
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite
import org.eclipse.jface.text.{BadLocationException, Document}

object FileWriter {
  def writeFile(rewriter: ASTRewrite, fileName: String) = {
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
      writeStringToFile(new File("/media/01D3908E9C0056A0/code/eclipse-workspace/cs474.test/src/cs474/test/Test.txt"), fileValue)
    }
    catch {
      case e: IOException =>
        e.printStackTrace()
    }
  }

}
