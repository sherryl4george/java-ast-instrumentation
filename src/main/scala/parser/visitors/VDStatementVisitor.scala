package parser.visitors

import org.eclipse.jdt.core.dom.{ASTVisitor, VariableDeclarationStatement}

/**
The VariableDeclarationStatement visitor.
 */
class VDStatementVisitor extends ASTVisitor{
  private[this] var vdStatements: List[VariableDeclarationStatement] = List()

  /**
   * visits all the VariableDeclarationStatement nodes in the AST.
   * @param node
   * @return
   */
  override def visit(node: VariableDeclarationStatement): Boolean = {
    vdStatements = vdStatements :+ node
    super.visit(node)
  }

  /**
   * Returns a list of VariableDeclarationStatements   *
   * @return
   */
  def getVariableDeclarationStatements: List[VariableDeclarationStatement] = vdStatements
}
