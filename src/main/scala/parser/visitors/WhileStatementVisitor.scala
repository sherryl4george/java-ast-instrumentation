package parser.visitors

import org.eclipse.jdt.core.dom.{ASTVisitor, WhileStatement}

class WhileStatementVisitor extends ASTVisitor{
  private[this] var whileStatements: List[WhileStatement] = List()

  override def visit(node: WhileStatement): Boolean = {
    whileStatements = whileStatements :+ node
    super.visit(node)
  }

  def getForStatements: List[WhileStatement] = whileStatements
}
