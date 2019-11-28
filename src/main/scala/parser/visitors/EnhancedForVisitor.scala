package parser.visitors

import org.eclipse.jdt.core.dom.{ASTVisitor, EnhancedForStatement}

class EnhancedForVisitor extends ASTVisitor{
  private[this] var forStatements: List[EnhancedForStatement] = List()

  override def visit(node: EnhancedForStatement): Boolean = {
    forStatements = forStatements :+ node
    super.visit(node)
  }

  def getForStatements: List[EnhancedForStatement] = forStatements
}