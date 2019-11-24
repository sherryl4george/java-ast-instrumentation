package parser.visitors

import org.eclipse.jdt.core.dom.{ASTVisitor, ExpressionStatement}

class ExpressionStatementVisitor extends ASTVisitor{
  private[this] var expressionStatements: List[ExpressionStatement] = List()

  override def visit(node: ExpressionStatement): Boolean = {
    expressionStatements = expressionStatements :+ node
    super.visit(node)
  }

  def getExpressionStatements: List[ExpressionStatement] = expressionStatements
}
