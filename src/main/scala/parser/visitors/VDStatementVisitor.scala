package parser.visitors

import org.eclipse.jdt.core.dom.{ASTVisitor, VariableDeclarationStatement}

class VDStatementVisitor extends ASTVisitor{
  private[this] var vdStatements: List[VariableDeclarationStatement] = List()

  override def visit(node: VariableDeclarationStatement): Boolean = {
    vdStatements = vdStatements :+ node
    super.visit(node)
  }

  def getExpressionStatements: List[VariableDeclarationStatement] = vdStatements
}
