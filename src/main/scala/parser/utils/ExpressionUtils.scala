package parser.utils

import org.eclipse.jdt.core.dom._
import scala.jdk.CollectionConverters._

//case class ExpressionUtils() {
//
//}

object ExpressionUtils{
  def getTextForExpression(expression: Expression): String = {
    expression match {
      case _: ArrayCreation => utils.wrapStringInQuotes("")
      case _: ArrayInitializer => utils.wrapStringInQuotes("")
      case _: ArrayAccess => utils.wrapStringInQuotes("ArrayAccess")
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

    def recurseExpressionHelper(expression: Expression, extra: String = ""): Unit = {
      expression match {
        case _: ArrayCreation => {}
        case x: ArrayAccess => {
          if(extra.isEmpty)
            attributes = attributes :+ new Attribute(utils.wrapStringInQuotes("Array Begin"),utils.wrapStringInQuotes(""),utils.wrapStringInQuotes("{"))
          recurseExpressionHelper(x.getArray,"inner ")
          recurseExpressionHelper(x.getIndex," inner ")
          if(extra.isEmpty)
            attributes = attributes :+ new Attribute(utils.wrapStringInQuotes("Array End"),utils.wrapStringInQuotes(""),utils.wrapStringInQuotes("}"))
        }
        case x :  ArrayInitializer => {
          val expressionList =  x.expressions().asScala.toList
          attributes = attributes :+ new Attribute(utils.wrapStringInQuotes("Array Begin"),utils.wrapStringInQuotes(""),utils.wrapStringInQuotes("{"))
          expressionList.foreach(x => recurseExpressionHelper(x.asInstanceOf[Expression]))
          attributes = attributes :+ new Attribute(utils.wrapStringInQuotes("Array End"),utils.wrapStringInQuotes(""),utils.wrapStringInQuotes("}"))

        }
        case x: Assignment => {
          val assignment = expression.asInstanceOf[Assignment]
          recurseExpressionHelper(assignment.getLeftHandSide)
          recurseExpressionHelper(assignment.getRightHandSide)
        }
        case x: ClassInstanceCreation => {
          var qualifiedName = new String
          val methodInvocation = expression.asInstanceOf[ClassInstanceCreation]
          if (methodInvocation.getExpression.isInstanceOf[QualifiedName])
            qualifiedName = methodInvocation.getExpression.asInstanceOf[QualifiedName].getFullyQualifiedName //+ "."
          else if (methodInvocation.getExpression.isInstanceOf[SimpleName])
            qualifiedName = methodInvocation.getExpression.asInstanceOf[SimpleName].getFullyQualifiedName //+ "."

          val (binding, methodSignature) = Binding.getBindingLabel(methodInvocation.resolveConstructorBinding())
          val newBinding = if(qualifiedName.length > 0) qualifiedName else binding
          if(newBinding.length > 0) {
            attributes = attributes :+ new Attribute(utils.wrapStringInQuotes(extra + "ClassInstanceCreation"), utils.wrapStringInQuotes(newBinding), utils.wrapStringInQuotes(methodSignature))
            //          attributes = attributes :+ new Attribute("MethodInvocation", utils.wrapStringInQuotes(binding), utils.wrapStringInQuotes(qualifiedName+methodInvocation.getName.toString))
            val args = methodInvocation.arguments().asScala.toList
            args.foreach(x => recurseExpressionHelper(x.asInstanceOf[Expression]))
          }
        }
        case _: ConditionalExpression => {}
        case x: FieldAccess => {
          val (binding, declaringMethod) = Binding.getBindingLabel(x.getName.resolveBinding())
          val sdf = x.getName.getFullyQualifiedName
          attributes = attributes :+ new Attribute(utils.wrapStringInQuotes(extra + "SimpleName"), utils.wrapStringInQuotes(binding),
            List(x.getExpression, x.getName.getFullyQualifiedName).mkString("."))
        }
        case x: InfixExpression => {
          recurseExpressionHelper(x.getLeftOperand(), extra);
          recurseExpressionHelper(x.getRightOperand(), extra);
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
          var finalBinding = ""
          var finalMethodSignature = ""
          if(methodSignature.contains(newBinding) && methodSignature.indexOf(newBinding) ==  methodSignature.lastIndexOf(newBinding)){
            finalBinding = methodSignature
            finalMethodSignature = ""
          }
          else{
            finalBinding =  if(methodSignature.length > 0) newBinding+"."+methodSignature else newBinding
            finalMethodSignature = ""
          }
          attributes = attributes :+ new Attribute(utils.wrapStringInQuotes(extra + "MethodInvocation"), utils.wrapStringInQuotes(finalBinding.replace("\"","")), utils.wrapStringInQuotes(finalMethodSignature.replace("\"","")))
          //          attributes = attributes :+ new Attribute("MethodInvocation", utils.wrapStringInQuotes(binding), utils.wrapStringInQuotes(qualifiedName+methodInvocation.getName.toString))
          val args = methodInvocation.arguments().asScala.toList
          args.foreach(x => recurseExpressionHelper(x.asInstanceOf[Expression], "args "))
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
          attributes = attributes :+ new Attribute(utils.wrapStringInQuotes(extra + "SimpleName"), utils.wrapStringInQuotes(binding+"."+expression.asInstanceOf[SimpleName].getFullyQualifiedName), expression.asInstanceOf[SimpleName].getFullyQualifiedName)
        }
        case x: PostfixExpression => {
          recurseExpressionHelper(x.getOperand)
        }
        case x: PrefixExpression => {
          val operator = x.getOperator
          operator match {
            case PrefixExpression.Operator.MINUS => {
              val operand = x.getOperand
              operand.getNodeType match
                {
                case ASTNode.NUMBER_LITERAL => {
                  attributes = attributes :+ new Attribute(utils.wrapStringInQuotes(extra + "NumberLiteral"), utils.wrapStringInQuotes(""), operator.toString + operand.asInstanceOf[NumberLiteral].getToken)
                }
                case _ => recurseExpressionHelper(operand)
              }
            }
            case _ => recurseExpressionHelper(x.getOperand)
          }
        }
        case x: QualifiedName => {
          val (binding, declaringMethod) = Binding.getBindingLabel(x.getName.resolveBinding())
          val sdf = x.getName.getFullyQualifiedName
          attributes = attributes :+ new Attribute(utils.wrapStringInQuotes(extra + "QualifiedName"), utils.wrapStringInQuotes(binding),
            List(x.getQualifier.getFullyQualifiedName, x.getName.getFullyQualifiedName).mkString("."))
        }
        case _: SuperMethodInvocation => {}
        case _: SuperMethodReference => {}
        case _: ThisExpression => {}
        case _: TypeMethodReference => {}
        case _: VariableDeclarationExpression => {}
        case x: NumberLiteral => {
          attributes = attributes :+ new Attribute(utils.wrapStringInQuotes(extra + "NumberLiteral"), utils.wrapStringInQuotes(""), x.getToken)
        }
        case _: NullLiteral => attributes = attributes :+ new Attribute(utils.wrapStringInQuotes(extra + "NullLiteral"), utils.wrapStringInQuotes(""), "null")
        case x: StringLiteral => attributes = attributes :+ new Attribute(utils.wrapStringInQuotes(extra + "StringLiteral"), utils.wrapStringInQuotes(""), x.getEscapedValue)
        case x: BooleanLiteral => attributes = attributes :+ new Attribute(utils.wrapStringInQuotes(extra + "BooleanLiteral"), utils.wrapStringInQuotes(""), x.booleanValue().toString)
        case x: CharacterLiteral => attributes = attributes :+ new Attribute(utils.wrapStringInQuotes(extra + "CharacterLiteral"), utils.wrapStringInQuotes(""), utils.wrapStringInQuotes(x.getEscapedValue))
        case _ => {}

      }
    }
    recurseExpressionHelper(expression)
    attributes
  }
}
