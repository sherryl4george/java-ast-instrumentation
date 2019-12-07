package parser.visitors

import org.eclipse.jdt.core.dom.{ASTVisitor, EnhancedForStatement}

/**
Visitor to visit Range based for statements.
 */

class EnhancedForVisitor extends ASTVisitor{
  private[this] var forStatements: List[EnhancedForStatement] = List()

  /**
   * visits the Enhanced ForStatement node in the AST.
   * @param node
   * @return
   */
  override def visit(node: EnhancedForStatement): Boolean = {
    forStatements = forStatements :+ node
    super.visit(node)
  }

  /**
   * Returns a list of visited forStatements.
   * @return
   */
  def getForStatements: List[EnhancedForStatement] = forStatements
}