package parser.instrumentation

import org.eclipse.jdt.core.dom._
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite
import parser.utils.{Attribute, ExpressionUtils, utils}
import parser.visitors.ExpressionStatementVisitor

class AssignmentInstrum(val cu: CompilationUnit) {
  private[this] val rewriter = ASTRewrite.create(cu.getAST)
  private[this] var attributes: List[Attribute] = List()

  def startInstrum(): ASTRewrite ={
    assignmentInstrum()
    rewriter
  }

  def assignmentInstrum(): Unit ={
    val expressionStatementVisitor = new ExpressionStatementVisitor
    cu.accept(expressionStatementVisitor)
    val expressionStatemtemts = expressionStatementVisitor.getExpressionStatements
    //    expressionStatemtemts.filter(_.getExpression.isInstanceOf[Assignment]).map(assignmentInstrumHelper(_))
    expressionStatemtemts.map(assignmentInstrumHelper(_))
  }

  def assignmentInstrumHelper(expressionStatement: ExpressionStatement) = {
    def getParent(parent: ASTNode): ASTNode = {
      if (parent.isInstanceOf[Block])
        parent
      else
        getParent(parent.getParent)
    }

    def getImmediateParent(parent: ASTNode): ASTNode ={
      if(parent.isInstanceOf[ExpressionStatement])
        parent
      else
        getParent(parent.getParent)
    }

    val parent = getParent(expressionStatement.getParent)

    val attributes: List[Attribute] = ExpressionUtils.recurseExpression(expressionStatement.getExpression)
    var log = ""
    if(attributes.length > 0) {
      log = "TemplateClass.instrum(" + cu.getLineNumber(expressionStatement.getStartPosition)
      log += ", " + ExpressionUtils.getTextForExpression(expressionStatement.getExpression)
      attributes.map(attribute => {
        log += ", new AP("+ attribute.expType + ", " + attribute.binding + ", " + attribute.variable + ")"
      }).mkString(", ")
      log += ");"
    }
    val siso = cu.getAST.newTextElement
    siso.setText(log)
    val lrw = rewriter.getListRewrite(parent, Block.STATEMENTS_PROPERTY)
    lrw.insertAfter(siso, expressionStatement, null)
  }


}
