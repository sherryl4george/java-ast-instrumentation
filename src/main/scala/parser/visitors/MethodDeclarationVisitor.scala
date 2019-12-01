package parser.visitors

import org.eclipse.jdt.core.dom.{ASTVisitor, MethodDeclaration}

class MethodDeclarationVisitor extends ASTVisitor{
  private[this] var methodDeclarations: List[MethodDeclaration] = List()

  override def visit(node: MethodDeclaration): Boolean = {
    methodDeclarations = methodDeclarations :+ node
    super.visit(node)
  }

  def getMethodDeclarations: List[MethodDeclaration] = methodDeclarations
}
