package parser.converters

import org.eclipse.jdt.core.dom._
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite
import parser.visitors.ForStatementVisitor


/** *
  * for
  *   x++
  * while(f() > 0);
  *
  * Needs to be converted to
  *
  * int v = 0;
  * for{
  *   x++;
  *   v = f()
  * }while(v > 0);
  *
  * Below is the Logic and class to for this
  * This wont convert complicated method invocations. It should be as simple as
  * having only one method invocation on either side of operator
  * f() + f() < 10 won't be handled in current implementation
  */
class ForStatementCon(val cu: CompilationUnit) {
    private[this] val rewriter = ASTRewrite.create(cu.getAST)

  def startBlockConvert(): ASTRewrite ={
    forBlock()
    rewriter
  }
  def forBlock(): Unit ={
    val forStatementVisitor = new ForStatementVisitor
    cu.accept(forStatementVisitor)
    val forStatements = forStatementVisitor.getForStatements
    forStatements.map(forBlockHelper(_))
  }

  def forBlockHelper(forStatement: ForStatement): Unit ={
    val expression = forStatement.getExpression
    val forBody = forStatement.getBody
    val parent = forStatement.getParent
    if (expression.isInstanceOf[InfixExpression]) {
      val leftOperand = expression.asInstanceOf[InfixExpression].getLeftOperand
      val rightOperand = expression.asInstanceOf[InfixExpression].getRightOperand
      convert(leftOperand, parent, forStatement)
      convert(rightOperand, parent, forStatement)
    }

    def convert(operand: Expression, parent: ASTNode, forStatement: ForStatement): Unit ={
      if (operand.isInstanceOf[MethodInvocation]) {
        val operandMethod = operand.asInstanceOf[MethodInvocation]
        val (newVDS, fragmentSimpleName) = methodInvocationToVariableDeclarationStatement(operandMethod)
        rewriter.getListRewrite(parent, Block.STATEMENTS_PROPERTY).insertBefore(newVDS, forStatement, null)
        rewriter.replace(operandMethod, fragmentSimpleName, null)
        val newAssignment = methodInvocationToAssignment(operandMethod,fragmentSimpleName)
        val lrw = rewriter.getListRewrite(forBody, Block.STATEMENTS_PROPERTY)
        lrw.insertLast(forStatement.getAST.newExpressionStatement(newAssignment), null)
      }
    }
    def methodInvocationToVariableDeclarationStatement(operand: MethodInvocation) = {
      val newMethodInvocation = rewriter.createCopyTarget(operand).asInstanceOf[MethodInvocation]
      val fragment = operand.getAST.newVariableDeclarationFragment
      val fragmentSimpleName = operand.getAST.newSimpleName("for"+ForStatementCon.increment())
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
      if (iTypeBinding != null && iTypeBinding.isPrimitive)
        newVDS.setType(operand.getAST.newPrimitiveType(PrimitiveType.toCode(iTypeBinding.getName)))
      else
        newVDS.setType(operand.getAST.newSimpleType(operand.getAST.newName(iTypeBinding.getName)))
      (newVDS, fragmentSimpleName)
    }

    def methodInvocationToAssignment(operand: MethodInvocation,fragmentName:SimpleName) = {
      val newAssigment = forStatement.getAST.newAssignment
      newAssigment.setLeftHandSide(forStatement.getAST.newSimpleName(fragmentName.getIdentifier))
      newAssigment.setOperator(Assignment.Operator.ASSIGN)
      newAssigment.setRightHandSide(rewriter.createCopyTarget(operand).asInstanceOf[MethodInvocation])
      newAssigment
    }
  }
}

object ForStatementCon{
  private[this] var variable = 0
  def increment():String = {
    variable = variable + 1
    variable.toString
  }
}

