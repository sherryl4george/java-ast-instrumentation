package parser.visitors

import org.eclipse.jdt.core.dom.{ASTVisitor, IfStatement}

/**
Visitor for If Statement
 */
class IfStatementVisitor extends ASTVisitor {
  private[this] var ifStatements: List[IfStatement] = List()

  /**
   * visits the IfStatement nodes
   * @param node
   * @return
   */
  override def visit(node: IfStatement): Boolean = {
    ifStatements = ifStatements :+ node
    super.visit(node)
  }

  /**
   * returns a list of visited If statements.
   * @return
   */
  def getIfStatements: List[IfStatement] = ifStatements
}