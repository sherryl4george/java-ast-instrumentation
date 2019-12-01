package parser.instrumentation

import org.eclipse.jdt.core.dom.rewrite.ASTRewrite
import org.eclipse.jdt.core.dom._
import parser.utils.{Attribute, ExpressionUtils, utils}
import parser.visitors.ReturnStatementVisitor

class ReturnInstrum (val cu: CompilationUnit) {
  private[this] val rewriter = ASTRewrite.create(cu.getAST)

  def startInstrum(): ASTRewrite ={
    returnInstrum()
    rewriter
  }

  def returnInstrum() = {
    val returnStatementVisitor = new ReturnStatementVisitor
    cu.accept(returnStatementVisitor)
    val returnStatemtemts = returnStatementVisitor.getReturnStatements
    returnStatemtemts.map(returnInstrumHelper(_))
  }

  def returnInstrumHelper(statement: ReturnStatement): Unit ={
    def getParent(parent: ASTNode): ASTNode = {
      if (parent.isInstanceOf[Block])
        parent
      else
        getParent(parent.getParent)
    }
    val parent = getParent(statement.getParent)
    val attributes: List[Attribute] = ExpressionUtils.recurseExpression(statement.getExpression)
    var log = ""
    if(attributes.length > 0) {
      log = "TemplateClass.instrum(" + cu.getLineNumber(statement.getStartPosition)
      log += ", " + utils.wrapStringInQuotes("ReturnStatement")
      attributes.map(attribute => {
        log += ", new AP("+ attribute.expType + ", " + attribute.binding + ", " + attribute.variable + ")"

      }).mkString(", ")
      log += ");"
    }
    val textToAdd = cu.getAST.newTextElement
    textToAdd.setText(log)
    val lrw = rewriter.getListRewrite(parent, Block.STATEMENTS_PROPERTY)
    lrw.insertBefore(textToAdd, statement, null)
  }
}
