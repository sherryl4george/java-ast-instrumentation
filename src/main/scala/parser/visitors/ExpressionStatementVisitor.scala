package parser.visitors

import org.eclipse.jdt.core.dom.{ASTVisitor, ExpressionStatement}

/**
Visitor to visit Expression statements.
 */
class ExpressionStatementVisitor extends ASTVisitor{
  private[this] var expressionStatements: List[ExpressionStatement] = List()

  /**
   * visits the ExpressionStatement node in the AST.
   * @param node
   * @return
   */
  override def visit(node: ExpressionStatement): Boolean = {
    expressionStatements = expressionStatements :+ node
    super.visit(node)
  }

  /**
   * Returns a list of visited Expression Statements.
   * @return
   */
  def getExpressionStatements: List[ExpressionStatement] = expressionStatements
}
