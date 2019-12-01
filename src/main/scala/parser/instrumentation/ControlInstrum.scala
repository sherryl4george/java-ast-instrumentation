package parser.instrumentation


import org.eclipse.jdt.core.dom._
import parser.utils.{Attribute, ExpressionUtils}

class ControlInstrum() {

  def controlInstrumHelper(statement: Statement) = {
    val (expression: Option[Expression], name: Option[String]) = statement.getNodeType match {
      case ASTNode.SWITCH_STATEMENT => (Some(statement.asInstanceOf[SwitchStatement].getExpression), Some("SwitchStatement"))
      case ASTNode.IF_STATEMENT => (Some(statement.asInstanceOf[IfStatement].getExpression), Some("IfStatement"))
      case ASTNode.FOR_STATEMENT => (Some(statement.asInstanceOf[ForStatement].getExpression), Some("ForStatement"))
      case ASTNode.ENHANCED_FOR_STATEMENT => (Some(statement.asInstanceOf[EnhancedForStatement].getExpression), Some("ForStatement"))
      case ASTNode.WHILE_STATEMENT => (Some(statement.asInstanceOf[WhileStatement].getExpression), Some("WhileStatement"))
      case ASTNode.DO_STATEMENT => (Some(statement.asInstanceOf[DoStatement].getExpression), Some("DoStatement"))
      case _ => (None, None)
    }

    val attributes: List[Attribute] = ExpressionUtils.recurseExpression(expression.get)
    (attributes, name.getOrElse("Unknown"))
  }
}

