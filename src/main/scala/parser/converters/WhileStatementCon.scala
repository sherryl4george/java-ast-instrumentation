package parser.converters

import com.typesafe.scalalogging.LazyLogging
import org.eclipse.jdt.core.dom._
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite
import parser.visitors.WhileStatementVisitor


/** The While Statement converter class.
  * while(f()>0) {
  *   x++;
  * }
  *
  * modified to
  *
  * while(f() > 0){
  *   x++;
  * }
  *
  * This wont convert complicated method invocations. It should be as simple as
  * having only one method invocation on either side of operator
  * Say, f() + f() < 10 won't be handled in the current implementation.
  */
class WhileStatementCon(val cu: CompilationUnit) extends LazyLogging {
  private[this] val rewriter = ASTRewrite.create(cu.getAST)

  /**
   * while conversion begin
   * @return
   */
  def startBlockConvert(): ASTRewrite = {
    logger.debug("Begin while block re-write")
    whileBlock()
    logger.debug("End while block re-write")
    rewriter
  }

  /**
   * visit and invoke the converter on each While statement.
   */
  def whileBlock(): Unit = {
    val whileStatementVisitor = new WhileStatementVisitor
    cu.accept(whileStatementVisitor)
    val whileStatemtemts = whileStatementVisitor.getWhileStatements
    logger.info("The while statements that will be considered for re-write include -",whileStatemtemts.length)
    whileStatemtemts.map(whileBlockHelper(_))
  }

  /**
   * The While statement converter that identifies method declarations
   * transforms it to variable declaration statement.
   * Creates a rewritten AST.
   * @param whileStatement
   */
  def whileBlockHelper(whileStatement: WhileStatement): Unit = {

    /**
     * Converts the left / right operands.
     * Identifies method declarations and converts it to Variable declaration statements.
     * Adds a new statement with the method invocation inside the do-while block.
     * Rewrites the AST.
     * @param operand
     * @param parent
     * @param whileStatement
     */
    def convert(operand: Expression, parent: ASTNode, whileStatement: WhileStatement): Unit = {
      if (operand.isInstanceOf[MethodInvocation]) {
        logger.debug("This while statement will be considered for re-write")
        val whileBody = whileStatement.getBody
        val operandMethod = operand.asInstanceOf[MethodInvocation]

        //X() < 2 ----> int wh1 = X();
        val (newVDS, fragmentSimpleName) = methodInvocationToVariableDeclarationStatement(operandMethod)
        rewriter.getListRewrite(parent, Block.STATEMENTS_PROPERTY).insertBefore(newVDS, whileStatement, null)
        rewriter.replace(operandMethod, fragmentSimpleName, null)

        //A new wh1 = X() is inserted inside the while loop, to account for iteration.
        val newAssignment = methodInvocationToAssignment(operandMethod, fragmentSimpleName)
        val lrw = rewriter.getListRewrite(whileBody, Block.STATEMENTS_PROPERTY)
        lrw.insertLast(whileStatement.getAST.newExpressionStatement(newAssignment), null)
      }
    }

    /**
     * Transforms a method invocation X() in the loop expression to T x = X();     *
     * @param operand
     * @return
     */
    def methodInvocationToVariableDeclarationStatement(operand: MethodInvocation) = {
      val newMethodInvocation = rewriter.createCopyTarget(operand).asInstanceOf[MethodInvocation]
      val fragment = operand.getAST.newVariableDeclarationFragment

      //Assign variable names.
      val fragmentSimpleName = operand.getAST.newSimpleName("wh" + WhileStatementCon.increment())
      fragment.setName(fragmentSimpleName)
      fragment.setInitializer(newMethodInvocation)
      val newVDS = operand.getAST.newVariableDeclarationStatement(fragment)

      //Set type for the declared variable.
      val iTypeBinding = operand.getName.resolveTypeBinding
      if (iTypeBinding.isPrimitive)
        newVDS.setType(operand.getAST.newPrimitiveType(PrimitiveType.toCode(iTypeBinding.getName)))
      else
        newVDS.setType(operand.getAST.newSimpleType(operand.getAST.newName(iTypeBinding.getName)))
      (newVDS, fragmentSimpleName)
    }

    /**
     * Creates a new assignment statement inside the loop body to account for iteration.
     * @param operand
     * @param fragmentName
     * @return
     */
    def methodInvocationToAssignment(operand: MethodInvocation, fragmentName: SimpleName) = {
      val newAssigment = whileStatement.getAST.newAssignment

      //use fragment name created in the Variable declaration statement creation step.
      newAssigment.setLeftHandSide(whileStatement.getAST.newSimpleName(fragmentName.getIdentifier))
      newAssigment.setOperator(Assignment.Operator.ASSIGN)

      //Right hand side becomes a method-invocation.
      newAssigment.setRightHandSide(rewriter.createCopyTarget(operand).asInstanceOf[MethodInvocation])
      newAssigment
    }

    val expression = whileStatement.getExpression
    val parent = whileStatement.getParent

    //For an infix expression of the type x() < 2, we convert the left and right operands.
    if (expression.isInstanceOf[InfixExpression]) {
      val leftOperand = expression.asInstanceOf[InfixExpression].getLeftOperand
      val rightOperand = expression.asInstanceOf[InfixExpression].getRightOperand
      convert(leftOperand, parent, whileStatement)
      convert(rightOperand, parent, whileStatement)
    }
  }
}

/**
 * Creates unique variable names for each variable created.
 */
object WhileStatementCon{
  private[this] var variable = 0

  /**
   * increments and returns a new variable for every execution.
   * @return
   */
  def increment():String = {
    variable = variable + 1
    variable.toString
  }
}
