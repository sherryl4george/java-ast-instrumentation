package parser.instrumentation
import com.typesafe.scalalogging.LazyLogging
import org.eclipse.jdt.core.dom._
import parser.utils.{Attribute, ExpressionUtils}

/**
 * The Assignment Instrumentor class
 * Identifies the attributes needed for each assignment instrumentation.
 */
class AssignmentInstrum extends LazyLogging {
  /**
   * Instrument all assignments by recursing on the assignment expression.
   * This returns a list of attributes that are used in the Instrumentation statement.
   * @param expressionStatement
   * @return
   */
  def assignmentInstrumHelper(expressionStatement: ExpressionStatement) = {
   val attributes: List[Attribute] = ExpressionUtils.recurseExpression(expressionStatement.getExpression)
    logger.info("Total attributes to be added in expression statement instrumentation - " + attributes.length)
   attributes
  }

}
