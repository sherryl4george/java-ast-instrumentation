package parser.visitors

import org.eclipse.jdt.core.dom.{ASTVisitor, DoStatement}

class DoStatementVisitor extends ASTVisitor{
  private[this] var doStatements: List[DoStatement] = List()

  override def visit(node: DoStatement): Boolean = {
    doStatements = doStatements :+ node
    super.visit(node)
  }

  def getDoStatements: List[DoStatement] = doStatements
}
