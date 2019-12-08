package parser.instrumentation

import com.typesafe.scalalogging.LazyLogging
import org.eclipse.jdt.core.dom._
import parser.utils.{Attribute, ExpressionUtils}

/**
 * The Control structures Instrumentor class.
 * Identifies the attributes needed for Switch, if-else if, while, for, do-while, for each control statements.
 */
class ControlInstrum() extends LazyLogging{
  /**
   * Returns the type of the statement and a list of attributes depending on the type of statement.
   * @param statement
   * @return
   */

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

    //Recurse on the expression part of each of these statements to identify appropriate attributes.
    val attributes: List[Attribute] = ExpressionUtils.recurseExpression(expression.get)
    logger.info("Total attributes to be added in instrumentation for statement type " + name.get + "is " + attributes.length)
    (attributes, name.getOrElse("Unknown"))
  }
}

