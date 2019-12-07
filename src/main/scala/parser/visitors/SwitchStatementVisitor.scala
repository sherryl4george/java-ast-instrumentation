package parser.visitors

import org.eclipse.jdt.core.dom.{ASTVisitor, IfStatement, SwitchStatement}

/**
Visitor to visit switch statements.
 */
class SwitchStatementVisitor extends ASTVisitor{
  private[this] var switchStatements: List[SwitchStatement] = List()

  /**
   * This method visits all the Switch statements in the AST.
   * @param node
   * @return
   */
  override def visit(node: SwitchStatement): Boolean = {
    switchStatements = switchStatements :+ node
    super.visit(node)
  }

  /**
   * Returns a list of visited switch statements.   *
   * @return
   */
  def getSwitchStatements: List[SwitchStatement] = switchStatements
}