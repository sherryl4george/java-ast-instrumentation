package parser.visitors

import org.eclipse.jdt.core.dom.{ASTVisitor, IfStatement}

class IfStatementVisitor extends ASTVisitor{
  private[this] var ifStatements: List[IfStatement] = List()

  override def visit(node: IfStatement): Boolean = {
    ifStatements = ifStatements :+ node
    super.visit(node)
  }

  def getIfStatements: List[IfStatement] = ifStatements
}