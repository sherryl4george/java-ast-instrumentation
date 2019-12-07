package parser.visitors

import org.eclipse.jdt.core.dom.{ASTVisitor, MethodDeclaration}

/**
Visitor to visit all method declarations.
 */
class MethodDeclarationVisitor extends ASTVisitor{
  private[this] var methodDeclarations: List[MethodDeclaration] = List()

  /**
   * This visits all the MethodDeclaration nodes.
   * @param node
   * @return
   */
  override def visit(node: MethodDeclaration): Boolean = {
    methodDeclarations = methodDeclarations :+ node
    super.visit(node)
  }

  /**
   * Returns a list of method declarations visited.
   * @return
   */
  def getMethodDeclarations: List[MethodDeclaration] = methodDeclarations
}
