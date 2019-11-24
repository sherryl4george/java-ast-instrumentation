package parser.visitors

import org.eclipse.jdt.core.dom.{ASTVisitor, ForStatement}

class ForStatementVisitor extends ASTVisitor{
  private[this] var forStatements: List[ForStatement] = List()

  override def visit(node: ForStatement): Boolean = {
    forStatements = forStatements :+ node
    super.visit(node)
  }

  def getForStatements: List[ForStatement] = forStatements
}
