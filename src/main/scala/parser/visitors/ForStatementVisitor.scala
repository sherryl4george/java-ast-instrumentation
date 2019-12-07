package parser.visitors

import org.eclipse.jdt.core.dom.{ASTVisitor, ForStatement}

/**
Visitor to visit For statements.
 */
class ForStatementVisitor extends ASTVisitor{
  private[this] var forStatements: List[ForStatement] = List()

  /**
   * visits the ForStatement node in the AST.
   * @param node
   * @return
   */
  override def visit(node: ForStatement): Boolean = {
    forStatements = forStatements :+ node
    super.visit(node)
  }

  /**
   * Returns a list of visited forStatements.
   * @return
   */
  def getForStatements: List[ForStatement] = forStatements
}
