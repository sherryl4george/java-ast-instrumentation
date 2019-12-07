package parser.visitors

import org.eclipse.jdt.core.dom.{ASTVisitor, WhileStatement}

/**
The while statement Visitor
 */
class WhileStatementVisitor extends ASTVisitor{
  private[this] var whileStatements: List[WhileStatement] = List()

  /**
   * Visits all the WhileStatements in the AST.
   * @param node
   * @return
   */
  override def visit(node: WhileStatement): Boolean = {
    whileStatements = whileStatements :+ node
    super.visit(node)
  }

  /**
   * Returns a list of while statements visited.
   * @return
   */
  def getWhileStatements: List[WhileStatement] = whileStatements
}
