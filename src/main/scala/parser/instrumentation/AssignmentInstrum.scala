package parser.instrumentation

import org.eclipse.jdt.core.dom._
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite
import parser.utils.{Attribute, Binding, utils}
import parser.visitors.ExpressionStatementVisitor

class AssignmentInstrum(val cu: CompilationUnit) {
  private[this] val rewriter = ASTRewrite.create(cu.getAST)
  private[this] var attributes: List[Attribute] = List()

  def startInstrum(): ASTRewrite ={
    assignmentInstrum()
    rewriter
  }

  def assignmentInstrum(): Unit ={
    val expressionStatementVisitor = new ExpressionStatementVisitor
    cu.accept(expressionStatementVisitor)
    val expressionStatemtemts = expressionStatementVisitor.getExpressionStatements
    expressionStatemtemts.filter(_.getExpression.isInstanceOf[Assignment]).map(assignmentInstrumHelper(_))
  }

  def assignmentInstrumHelper(expressionStatement: ExpressionStatement) = {
    def getParent(parent: ASTNode): ASTNode = {
      if (parent.isInstanceOf[Block])
        parent
      else
        getParent(parent.getParent)
    }

    def getImmediateParent(parent: ASTNode): ASTNode ={
      if(parent.isInstanceOf[ExpressionStatement])
        parent
      else
        getParent(parent.getParent)
    }
    val assignment = expressionStatement.getExpression.asInstanceOf[Assignment]
    attributes = List()
    val parent = getParent(assignment.getParent)
    val immediateParent = getParent(assignment.getParent)

    recurseExpression(assignment.getLeftHandSide)
    recurseExpression(assignment.getRightHandSide)
    var log = "TemplateClass.instrum(" + cu.getLineNumber(assignment.getStartPosition)
    log += ", " + "\"Assign\""
    for (attribute <- attributes) {
      log += ", new AP(" + attribute.binding +", "+ attribute.variable + ")"
    }
    log += ");"
    val siso = cu.getAST.newTextElement
    siso.setText(log)
    val lrw = rewriter.getListRewrite(parent, Block.STATEMENTS_PROPERTY)
    lrw.insertAfter(siso, expressionStatement, null)
  }

  def recurseExpression(expression: Expression): Unit = {
    expression match {
      case _ :ArrayCreation => {}
      case _ :ArrayInitializer => {}
      case _ :Assignment => {}
      case _ :ClassInstanceCreation => {}
      case _ :ConditionalExpression => {}
      case _ :FieldAccess => {
        if(expression.asInstanceOf[FieldAccess].getExpression.isInstanceOf[ThisExpression]){
          println((expression.asInstanceOf[FieldAccess]).getExpression.asInstanceOf[ThisExpression].getQualifier)
        }
      }
      case _ :InfixExpression => {}
      case _ :MethodInvocation => {
        var qualifiedName = new String
        val methodInvocation = expression.asInstanceOf[MethodInvocation]
        if (methodInvocation.getExpression.isInstanceOf[QualifiedName])
          qualifiedName = methodInvocation.getExpression.asInstanceOf[QualifiedName].getFullyQualifiedName + "."
        else if (methodInvocation.getExpression.isInstanceOf[SimpleName])
          qualifiedName = methodInvocation.getExpression.asInstanceOf[SimpleName].getFullyQualifiedName + "."

        val (binding, methodSignature) = Binding.getBindingLabel(methodInvocation.resolveMethodBinding())
        attributes = attributes:+ new Attribute("MethodInvocation", utils.wrapStringInQuotes(binding), utils.wrapStringInQuotes(methodSignature))
      }
      case _ :ParenthesizedExpression => {}
      case _ :MethodReference => {}
      case _ :SimpleName => {
        val simpleName: SimpleName = expression.asInstanceOf[SimpleName]
        val test = simpleName.toString
        val (binding, declaringMethod) = Binding.getBindingLabel(simpleName.resolveBinding())
        val sdf = expression.asInstanceOf[SimpleName].getFullyQualifiedName
        attributes = attributes :+ new Attribute("SimpleName", utils.wrapStringInQuotes(binding), expression.asInstanceOf[SimpleName].getFullyQualifiedName)
        println()
      }
      case _ :PostfixExpression => {}
      case _ :PrefixExpression => {}
      case x :QualifiedName => recurseExpression(x.asInstanceOf[QualifiedName].getName)
      case _ :SuperMethodInvocation => {}
      case _ :SuperMethodReference => {}
      case _ :ThisExpression => {}
      case _ :TypeMethodReference => {}
      case _ :VariableDeclarationExpression => {}
      case _  => {}

    }
    //    if (expression.isInstanceOf[ArrayAccess]) {
    //    }
    //    else if (expression.isInstanceOf[ArrayCreation]) {
    //    }
    //    else if (expression.isInstanceOf[ArrayCreation]) {
    //    }
    //    else if (expression.isInstanceOf[ArrayInitializer]) {
    //    }
    //    else if (expression.isInstanceOf[Assignment]) {
    //    }
    //    else if (expression.isInstanceOf[ClassInstanceCreation]) {
    //    }
    //    else if (expression.isInstanceOf[ConditionalExpression]) {
    //    }
    //    else if (expression.isInstanceOf[FieldAccess]) if (expression.asInstanceOf[FieldAccess].getExpression.isInstanceOf[ThisExpression]) System.out.println((expression.asInstanceOf[FieldAccess]).getExpression.asInstanceOf[ThisExpression].getQualifier)
    //    else if (expression.isInstanceOf[InfixExpression]) {
    //      val lhs = expression.asInstanceOf[InfixExpression].getLeftOperand
    //      recurseExpression(lhs)
    //      val rhs = expression.asInstanceOf[InfixExpression].getRightOperand
    //      recurseExpression(rhs)
    //    }
    //    else if (expression.isInstanceOf[NumberLiteral])
    //      attributes:+ new Attribute("", "NumberLiteral", expression.resolveConstantExpressionValue.toString)
    ////    else if (expression.isInstanceOf[InstanceofExpression]) {
    ////    }
    ////    else if (expression.isInstanceOf[LambdaExpression]) {
    ////    }
    //    else if (expression.isInstanceOf[MethodInvocation]) {
    //      var qualifiedName = new String
    //      val methodInvocation = expression.asInstanceOf[MethodInvocation]
    //      if (methodInvocation.getExpression.isInstanceOf[QualifiedName]) qualifiedName = methodInvocation.getExpression.asInstanceOf[QualifiedName].getFullyQualifiedName + "."
    //      attributes:+(new Attribute("",", "\"" + qualifiedName + expression.asInstanceOf[MethodInvocation].getName.toString + "()" + "\""))
    //      //getFullName(((MethodInvocation) expression).getExpression(), "");
    //      System.out.println()
    //      System.out.println()
    //    }
    //    else if (expression.isInstanceOf[ParenthesizedExpression]) recurseExpression(expression.asInstanceOf[ParenthesizedExpression].getExpression)
    //    else if (expression.isInstanceOf[MethodReference]) {
    //    }
    //    else if (expression.isInstanceOf[SimpleName]) {
    //      attributes:+(new Attribute((expression.asInstanceOf[SimpleName]).resolveBinding.asInstanceOf[IVariableBinding].getDeclaringMethod.getDeclaringClass.getQualifiedName, expression.asInstanceOf[SimpleName].getFullyQualifiedName))
    //      System.out.println()
    //    }
    //    else if (expression.isInstanceOf[ParenthesizedExpression]) {
    //    }
    //    else if (expression.isInstanceOf[PostfixExpression]) {
    //    }
    //    else if (expression.isInstanceOf[PrefixExpression]) {
    //    }
    //    else if (expression.isInstanceOf[SuperMethodInvocation]) {
    //    }
    //    else if (expression.isInstanceOf[SuperMethodReference]) {
    //    }
    //    else if (expression.isInstanceOf[ThisExpression]) {
    //    }
    //    else if (expression.isInstanceOf[TypeMethodReference]) {
    //    }
    //    else if (expression.isInstanceOf[VariableDeclarationExpression]) {
    //    }
  }
}
