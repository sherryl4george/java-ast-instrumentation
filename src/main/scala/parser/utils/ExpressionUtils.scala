package parser.utils

import org.eclipse.jdt.core.dom._
import scala.jdk.CollectionConverters._

case class ExpressionUtils() {

}

object ExpressionUtils{
  def getTextForExpression(expression: Expression): String = {
    expression match {
      case _: ArrayCreation => utils.wrapStringInQuotes("")
      case _: ArrayInitializer => utils.wrapStringInQuotes("")
      case x: Assignment => utils.wrapStringInQuotes("Assign")
      case _: ClassInstanceCreation => utils.wrapStringInQuotes("ClassInstanceCreation")
      case _: ConditionalExpression => utils.wrapStringInQuotes("")
      case x: FieldAccess => utils.wrapStringInQuotes("")
      case _: InfixExpression => utils.wrapStringInQuotes("")
      case _: MethodInvocation => utils.wrapStringInQuotes("MethodInvocation")
      case _: ParenthesizedExpression => utils.wrapStringInQuotes("")
      case _: MethodReference => utils.wrapStringInQuotes("")
      case _: SimpleName => utils.wrapStringInQuotes("")
      case _: PostfixExpression => utils.wrapStringInQuotes("")
      case _: PrefixExpression => utils.wrapStringInQuotes("")
      case x: QualifiedName => utils.wrapStringInQuotes("")
      case _: SuperMethodInvocation => utils.wrapStringInQuotes("")
      case _: SuperMethodReference => utils.wrapStringInQuotes("")
      case _: ThisExpression => utils.wrapStringInQuotes("")
      case _: TypeMethodReference => utils.wrapStringInQuotes("")
      case _: VariableDeclarationExpression => utils.wrapStringInQuotes("")
      case x: NumberLiteral => utils.wrapStringInQuotes("")
      case _: NullLiteral => utils.wrapStringInQuotes("")
      case _ => utils.wrapStringInQuotes("")

    }
  }

  def recurseExpression(expression: Expression): List[Attribute] ={
    var attributes: List[Attribute] = List()

    def recurseExpressionHelper(expression: Expression): Unit = {
      expression match {
        case _: ArrayCreation => {}
        case _: ArrayInitializer => {}
        case x: Assignment => {
          val assignment = expression.asInstanceOf[Assignment]
          recurseExpressionHelper(assignment.getLeftHandSide)
          recurseExpressionHelper(assignment.getRightHandSide)
        }
        case x: ClassInstanceCreation => {
          println(x)
        }
        case _: ConditionalExpression => {}
        case x: FieldAccess => {
          println(x.getExpression)
          println(x.getName)
          val (binding, declaringMethod) = Binding.getBindingLabel(x.getName.resolveBinding())
          val sdf = x.getName.getFullyQualifiedName
          attributes = attributes :+ new Attribute(utils.wrapStringInQuotes("SimpleName"), utils.wrapStringInQuotes(binding),
            List(x.getExpression, x.getName.getFullyQualifiedName).mkString("."))
        }
        case x: InfixExpression => {
          recurseExpressionHelper(x.getLeftOperand());
          recurseExpressionHelper(x.getRightOperand());
        }
        case _: MethodInvocation => {
          var qualifiedName = new String
          val methodInvocation = expression.asInstanceOf[MethodInvocation]
          if (methodInvocation.getExpression.isInstanceOf[QualifiedName])
            qualifiedName = methodInvocation.getExpression.asInstanceOf[QualifiedName].getFullyQualifiedName //+ "."
          else if (methodInvocation.getExpression.isInstanceOf[SimpleName])
            qualifiedName = methodInvocation.getExpression.asInstanceOf[SimpleName].getFullyQualifiedName //+ "."

          val (binding, methodSignature) = Binding.getBindingLabel(methodInvocation.resolveMethodBinding())
          val newBinding = if(qualifiedName.length > 0) qualifiedName else binding
          attributes = attributes :+ new Attribute(utils.wrapStringInQuotes("MethodInvocation"), utils.wrapStringInQuotes(newBinding), utils.wrapStringInQuotes(methodSignature))
//          attributes = attributes :+ new Attribute("MethodInvocation", utils.wrapStringInQuotes(binding), utils.wrapStringInQuotes(qualifiedName+methodInvocation.getName.toString))
          val args = methodInvocation.arguments().asScala.toList
          args.foreach(x => recurseExpressionHelper(x.asInstanceOf[Expression]))
        }
        case x: ParenthesizedExpression => {
          recurseExpressionHelper(x.getExpression)
        }
        case _: MethodReference => {}
        case _: SimpleName => {
          val simpleName: SimpleName = expression.asInstanceOf[SimpleName]
          val test = simpleName.toString
          val (binding, declaringMethod) = Binding.getBindingLabel(simpleName.resolveBinding())
          val sdf = expression.asInstanceOf[SimpleName].getFullyQualifiedName
          attributes = attributes :+ new Attribute(utils.wrapStringInQuotes("SimpleName"), utils.wrapStringInQuotes(binding), expression.asInstanceOf[SimpleName].getFullyQualifiedName)
        }
        case _: PostfixExpression => {}
        case _: PrefixExpression => {}
        case x: QualifiedName => {
          val (binding, declaringMethod) = Binding.getBindingLabel(x.getName.resolveBinding())
          val sdf = x.getName.getFullyQualifiedName
          attributes = attributes :+ new Attribute(utils.wrapStringInQuotes("QualifiedName"), utils.wrapStringInQuotes(binding),
            List(x.getQualifier.getFullyQualifiedName, x.getName.getFullyQualifiedName).mkString("."))
        }
        case _: SuperMethodInvocation => {}
        case _: SuperMethodReference => {}
        case _: ThisExpression => {}
        case _: TypeMethodReference => {}
        case _: VariableDeclarationExpression => {}
        case x: NumberLiteral => {
          attributes = attributes :+ new Attribute(utils.wrapStringInQuotes("NumberLiteral"), utils.wrapStringInQuotes(""), x.getToken)
        }
        case _: NullLiteral => attributes = attributes :+ new Attribute(utils.wrapStringInQuotes("NullLiteral"), utils.wrapStringInQuotes(""), "null")
        case _ => {}

      }
    }
    recurseExpressionHelper(expression)
    attributes
  }
}
