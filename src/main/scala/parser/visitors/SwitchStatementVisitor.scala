package parser.visitors

import org.eclipse.jdt.core.dom.{ASTVisitor, IfStatement, SwitchStatement}

class SwitchStatementVisitor extends ASTVisitor{
  private[this] var switchStatements: List[SwitchStatement] = List()

  override def visit(node: SwitchStatement): Boolean = {
    switchStatements = switchStatements :+ node
    super.visit(node)
  }

  def getSwitchStatements: List[SwitchStatement] = switchStatements
}