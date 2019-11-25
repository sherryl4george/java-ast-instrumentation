package parser.visitors

import org.eclipse.jdt.core.dom.{ASTVisitor, ReturnStatement}

class ReturnStatementVisitor extends ASTVisitor{
  private[this] var returnStatements: List[ReturnStatement] = List()

  override def visit(node: ReturnStatement): Boolean = {
    returnStatements = returnStatements :+ node
    super.visit(node)
  }

  def getReturnStatements: List[ReturnStatement] = returnStatements
}
