package parser.visitors

import org.eclipse.jdt.core.dom.{ASTVisitor, ReturnStatement}

/**
Visitor that visits all the Return statements.
 */
class ReturnStatementVisitor extends ASTVisitor{
  private[this] var returnStatements: List[ReturnStatement] = List()

  /**
   * Visits Return statements in an AST.
   * @param node
   * @return
   */
  override def visit(node: ReturnStatement): Boolean = {
    returnStatements = returnStatements :+ node
    super.visit(node)
  }

  /**
   * Returns a list of visited Return statements.
   * @return
   */
  def getReturnStatements: List[ReturnStatement] = returnStatements
}
