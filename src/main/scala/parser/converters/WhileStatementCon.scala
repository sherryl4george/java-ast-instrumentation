package parser.converters

import org.eclipse.jdt.core.dom._
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite
import parser.visitors.WhileStatementVisitor


/** *
  * while(f()>0)
  *   x++;
  *
  * Needs to be converted to
  *
  * while(f() > 0){
  *   x++;
  * }
  * Below is the Logic and class to do this
  * This wont convert complicated method invocations. It should be as simple as
  * having only one method invocation on either side of operator
  * f() + f() < 10 won't be handled in current implementataion
  */
class WhileStatementCon(val cu: CompilationUnit) {
  private[this] val rewriter = ASTRewrite.create(cu.getAST)

  def startBlockConvert(): ASTRewrite ={
    whileBlock()
    rewriter
  }
  def whileBlock(): Unit ={
    val whileStatementVisitor = new WhileStatementVisitor
    cu.accept(whileStatementVisitor)
    val whileStatemtemts = whileStatementVisitor.getWhileStatements
    whileStatemtemts.map(whileBlockHelper(_))
  }

  def whileBlockHelper(whileStatement: WhileStatement): Unit ={
    val expression = whileStatement.getExpression
    val whileBody = whileStatement.getBody
    val parent = whileStatement.getParent
    if (expression.isInstanceOf[InfixExpression]) {
      val leftOperand = expression.asInstanceOf[InfixExpression].getLeftOperand
      val rightOperand = expression.asInstanceOf[InfixExpression].getRightOperand

      convert(leftOperand, parent, whileStatement)
      convert(rightOperand, parent, whileStatement)
    }

    def convert(operand: Expression, parent: ASTNode, whileStatement: WhileStatement): Unit ={
      if (operand.isInstanceOf[MethodInvocation]) {
        val operandMethod = operand.asInstanceOf[MethodInvocation]
        val (newVDS, fragmentSimpleName) = methodInvocationToVariableDeclarationStatement(operandMethod)
        rewriter.getListRewrite(parent, Block.STATEMENTS_PROPERTY).insertBefore(newVDS, whileStatement, null)
        rewriter.replace(operandMethod, fragmentSimpleName, null)
        val newAssignment = methodInvocationToAssignment(operandMethod)
        val newWhileBody = whileToBody(whileBody)
        val lrw = rewriter.getListRewrite(newWhileBody, Block.STATEMENTS_PROPERTY)
        lrw.insertLast(whileStatement.getAST.newExpressionStatement(newAssignment), null)
      }
    }
    def methodInvocationToVariableDeclarationStatement(operand: MethodInvocation) = {
      val newMethodInvocation = rewriter.createCopyTarget(operand).asInstanceOf[MethodInvocation]
      val fragment = operand.getAST.newVariableDeclarationFragment
      val fragmentSimpleName = operand.getAST.newSimpleName("con")
      fragment.setName(fragmentSimpleName)
      fragment.setInitializer(newMethodInvocation)
      val newVDS = operand.getAST.newVariableDeclarationStatement(fragment)
      val iTypeBinding = operand.getName.resolveTypeBinding
      if (iTypeBinding.isPrimitive)
        newVDS.setType(operand.getAST.newPrimitiveType(PrimitiveType.toCode(iTypeBinding.getName)))
      else
        newVDS.setType(operand.getAST.newSimpleType(operand.getAST.newName(iTypeBinding.getName)))
      (newVDS, fragmentSimpleName)
    }

    def methodInvocationToAssignment(operand: MethodInvocation) = {
      val newAssigment = whileStatement.getAST.newAssignment
      newAssigment.setLeftHandSide(whileStatement.getAST.newSimpleName("con"))
      newAssigment.setOperator(Assignment.Operator.ASSIGN)
      newAssigment.setRightHandSide(rewriter.createCopyTarget(operand).asInstanceOf[MethodInvocation])
      newAssigment
    }

    def whileToBody(whileBody: Statement) = if (!whileBody.isInstanceOf[Block]) {
      val block = whileStatement.getAST.newBlock
      val lrw = rewriter.getListRewrite(block, Block.STATEMENTS_PROPERTY)
      lrw.insertLast(whileBody, null)
      rewriter.replace(whileBody, block, null)
      block
    }
    else
      whileBody
  }
}
