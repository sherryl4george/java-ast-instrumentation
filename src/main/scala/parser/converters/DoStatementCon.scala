package parser.converters

import com.typesafe.scalalogging.LazyLogging
import org.eclipse.jdt.core.dom._
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite
import parser.visitors.DoStatementVisitor

/** The Do-Statement converter class. This converts
  * do {
  *   x++;
  * } while(f() > 0);
  *
  * Modified to --------->
  *
  * int v = 0;
  * do{
  *   x++;
  *   v = f()
  * }while(v > 0);
  *
  * This wont convert complicated method invocations. It should be as simple as
  * having only one method invocation on either side of operator
  * f() + f() < 10 won't be handled in the current implementation
  */
class DoStatementCon(val cu: CompilationUnit) extends LazyLogging {
    private[this] val rewriter = ASTRewrite.create(cu.getAST)

  /**
   * do-conversion begin
   * @return
   */
  def startBlockConvert(): ASTRewrite ={
    logger.debug("Do statement rewrite begin")
    doBlock()
    logger.debug("Do statement rewrite end")
    rewriter
  }

  /**
   * visit and invoke the converter on each do statement.
   */
  def doBlock(): Unit ={
    val doStatementVisitor = new DoStatementVisitor
    cu.accept(doStatementVisitor)
    val doStatements = doStatementVisitor.getDoStatements
    logger.info("Total do-statements to be considered for re-writing - " + doStatements.length)
    doStatements.map(doBlockHelper(_))
  }

  /**
   * The Do-statement converter that identifies method declarations
   * transforms it to variable declaration statement.
   * Creates a rewritten AST.
   * @param doStatement
   */
  def doBlockHelper(doStatement: DoStatement): Unit ={
    val expression = doStatement.getExpression
    val doBody = doStatement.getBody
    val parent = doStatement.getParent

    //For an infix expression of the type x() < 2, we convert the left and right operands.
    if (expression.isInstanceOf[InfixExpression]) {
      val leftOperand = expression.asInstanceOf[InfixExpression].getLeftOperand
      val rightOperand = expression.asInstanceOf[InfixExpression].getRightOperand
      convert(leftOperand, parent, doStatement)
      convert(rightOperand, parent, doStatement)
    }

    /**
     * Converts the left / right operands.
     * Identifies method declarations and converts it to Variable declaration statements.
     * Adds a new statement with the method invocation inside the do-while block.
     * Rewrites the AST.
     * @param operand
     * @param parent
     * @param doStatement
     */
    def convert(operand: Expression, parent: ASTNode, doStatement: DoStatement): Unit ={
      if (operand.isInstanceOf[MethodInvocation]) {
        logger.info("Found a candidate for re-write do statement")
        val operandMethod = operand.asInstanceOf[MethodInvocation]

        //X() < 2 ----> int wh1 = X();
        val (newVDS, fragmentSimpleName) = methodInvocationToVariableDeclarationStatement(operandMethod)
        rewriter.getListRewrite(parent, Block.STATEMENTS_PROPERTY).insertBefore(newVDS, doStatement, null)
        rewriter.replace(operandMethod, fragmentSimpleName, null)

        //A new wh1 = X() is inserted inside the do-while loop, to account for iteration.
        val newAssignment = methodInvocationToAssignment(operandMethod,fragmentSimpleName)
        val lrw = rewriter.getListRewrite(doBody, Block.STATEMENTS_PROPERTY)
        lrw.insertLast(doStatement.getAST.newExpressionStatement(newAssignment), null)
      }
      logger.debug("Conversion for operand ends")
    }

    /**
     * Transforms a method invocation X() in the loop expression to T x = X();
     * @param operand
     * @return
     */
    def methodInvocationToVariableDeclarationStatement(operand: MethodInvocation) = {
      val newMethodInvocation = rewriter.createCopyTarget(operand).asInstanceOf[MethodInvocation]
      //Assign variable names.
      val fragment = operand.getAST.newVariableDeclarationFragment
      val fragmentSimpleName = operand.getAST.newSimpleName("do" + DoStatementCon.increment())
      val iTypeBinding = operand.getName.resolveTypeBinding
      fragment.setName(fragmentSimpleName)

      //Set type initializer. int = 0, reference types = null.
      if(iTypeBinding.isPrimitive){
        val pType = PrimitiveType.toCode(iTypeBinding.getName())
        pType match {
          case PrimitiveType.BOOLEAN => fragment.setInitializer(fragment.getAST.newBooleanLiteral(false))
          case _=> fragment.setInitializer(fragment.getAST.newNumberLiteral("0"))
        }
      }
      else
        fragment.setInitializer(fragment.getAST.newNullLiteral())

      //assign type name to statement.
      val newVDS = operand.getAST.newVariableDeclarationStatement(fragment)
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
      val newAssigment = doStatement.getAST.newAssignment

      //use fragment name created in the Variable declaration statement creation step.
      newAssigment.setLeftHandSide(doStatement.getAST.newSimpleName(fragmentName.getIdentifier))
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
object DoStatementCon{
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
