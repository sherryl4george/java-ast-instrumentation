package parser.converters

import com.typesafe.scalalogging.LazyLogging
import org.eclipse.jdt.core.dom._
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite
import parser.visitors.ForStatementVisitor


/** The For statement converter class.
  * for(int i=0; X() < 2; i++){
  * System.out.println(i);
  * }
  *
  *  Modified to
  *
  * int v = X();
  * for(int i = 0; v < 2; i++) {
  * System.out.println(i);
  *   v = X();
  * }
  *
  *
  * This wont convert complicated method invocations. It should be as simple as
  * having only one method invocation on either side of operator.
  * Also, only an expression in the For statement is converted. We do not change the initializer and updater.
  * f() + f() < 10 won't be handled in current implementation
  */
class ForStatementCon(val cu: CompilationUnit) extends LazyLogging {
    private[this] val rewriter = ASTRewrite.create(cu.getAST)

  /**
   * for conversion begin.
   * @return
   */
  def startBlockConvert(): ASTRewrite ={
    logger.debug("Begin For Block re-write")
    forBlock()
    logger.debug("For block re-write ends")
    rewriter
  }

  /**
   * visit and invoke the converter on each for statement.
   */
  def forBlock(): Unit ={
    val forStatementVisitor = new ForStatementVisitor
    cu.accept(forStatementVisitor)
    val forStatements = forStatementVisitor.getForStatements
    logger.info("Total number of for-statements to be considered for re-writing - " + forStatements.length)
    forStatements.map(forBlockHelper(_))
  }

  /**
   * The for statement converter that identifies method declarations
   * transforms it to variable declaration statement.
   * Creates a rewritten AST.
   * @param forStatement
   */
  def forBlockHelper(forStatement: ForStatement): Unit ={
    val expression = forStatement.getExpression
    val forBody = forStatement.getBody
    val parent = forStatement.getParent

    //For an infix expression of the type x() < 2, we convert the left and right operands.
    if (expression.isInstanceOf[InfixExpression]) {
      val leftOperand = expression.asInstanceOf[InfixExpression].getLeftOperand
      val rightOperand = expression.asInstanceOf[InfixExpression].getRightOperand
      convert(leftOperand, parent, forStatement)
      convert(rightOperand, parent, forStatement)
    }

    /**
     * Identifies method declarations and converts it to Variable declaration statements.
     * Adds a new statement with the method invocation inside the do-while block.
     * Rewrites the AST.
     * @param operand
     * @param parent
     * @param forStatement
     */
    def convert(operand: Expression, parent: ASTNode, forStatement: ForStatement): Unit ={
      if (operand.isInstanceOf[MethodInvocation]) {
        logger.info("This For-statement is considered for re-writing")
        val operandMethod = operand.asInstanceOf[MethodInvocation]

        //X() < 2 ----> int for1 = X();
        val (newVDS, fragmentSimpleName) = methodInvocationToVariableDeclarationStatement(operandMethod)
        rewriter.getListRewrite(parent, Block.STATEMENTS_PROPERTY).insertBefore(newVDS, forStatement, null)
        rewriter.replace(operandMethod, fragmentSimpleName, null)

        //A new wh1 = X() is inserted inside the do-while loop, to account for iteration.
        val newAssignment = methodInvocationToAssignment(operandMethod,fragmentSimpleName)
        val lrw = rewriter.getListRewrite(forBody, Block.STATEMENTS_PROPERTY)
        lrw.insertLast(forStatement.getAST.newExpressionStatement(newAssignment), null)
      }
    }

    /**
     * Transforms a method invocation X() in the loop expression to T x = X();
     * @param operand
     * @return
     */
    def methodInvocationToVariableDeclarationStatement(operand: MethodInvocation) = {
      val newMethodInvocation = rewriter.createCopyTarget(operand).asInstanceOf[MethodInvocation]
      val fragment = operand.getAST.newVariableDeclarationFragment

      //Assign variable names.
      val fragmentSimpleName = operand.getAST.newSimpleName("for"+ForStatementCon.increment())

      //Set type initializer. int = 0, reference types = null.
      val iTypeBinding = operand.getName.resolveTypeBinding
      fragment.setName(fragmentSimpleName)
      if(iTypeBinding != null && iTypeBinding.isPrimitive){
        val pType = PrimitiveType.toCode(iTypeBinding.getName())
        pType match {
          case PrimitiveType.BOOLEAN => fragment.setInitializer(fragment.getAST.newBooleanLiteral(false))
          case _=> fragment.setInitializer(fragment.getAST.newNumberLiteral("0"))
        }
      }
      else
        fragment.setInitializer(fragment.getAST.newNullLiteral())
      val newVDS = operand.getAST.newVariableDeclarationStatement(fragment)

      //assign type name to statement.
      if (iTypeBinding != null && iTypeBinding.isPrimitive)
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
    def methodInvocationToAssignment(operand: MethodInvocation,fragmentName:SimpleName) = {
      val newAssigment = forStatement.getAST.newAssignment

      //use fragment name created in the Variable declaration statement creation step.
      newAssigment.setLeftHandSide(forStatement.getAST.newSimpleName(fragmentName.getIdentifier))
      newAssigment.setOperator(Assignment.Operator.ASSIGN)

      //Right hand side becomes a method-invocation.
      newAssigment.setRightHandSide(rewriter.createCopyTarget(operand).asInstanceOf[MethodInvocation])
      newAssigment
    }
  }
}

/**
 * Creates unique variable names for each variable created.
 */
object ForStatementCon{
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

