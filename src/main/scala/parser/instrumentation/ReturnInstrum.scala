package parser.instrumentation
import com.typesafe.scalalogging.LazyLogging
import org.eclipse.jdt.core.dom._
import parser.utils.{Attribute, ExpressionUtils}

/**
 * The Return Instrumentor class
 * Identifies the attributes needed for each return instrumentation.
 */
class ReturnInstrum extends LazyLogging{

  /**
   * Identifies all return statements by recursing on the return expression.
   * This returns a list of attributes that are used in the Instrumentation statement.
   * @param statement
   * @return
   */
  def returnInstrumHelper(statement: ReturnStatement) = {
    val attributes: List[Attribute] = ExpressionUtils.recurseExpression(statement.getExpression)
    logger.info("Total attributes to be added in return instrumentation - " + attributes.length)
    attributes
  }
}
