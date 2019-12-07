package parser.visitors
import org.eclipse.jdt.core.dom.{ASTVisitor, DoStatement}

/**
Visitor to visit Do-while statements.
 */
class DoStatementVisitor extends ASTVisitor{
  private[this] var doStatements: List[DoStatement] = List()

  /**
   * visits the DoStatement node in the AST.
   * @param node
   * @return
   */
  override def visit(node: DoStatement): Boolean = {
    doStatements = doStatements :+ node
    super.visit(node)
  }


  /**
   * Returns a list of visited doStatements.
   * @return
   */
  def getDoStatements: List[DoStatement] = doStatements
}
