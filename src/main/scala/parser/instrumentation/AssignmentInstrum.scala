package parser.instrumentation
import org.eclipse.jdt.core.dom._
import parser.utils.{Attribute, ExpressionUtils}

/**
 * The Assignment Instrumentor class
 * Identifies the attributes needed for each assignment instrumentation.
 */
class AssignmentInstrum {
  /**
   * Instrument all assignments by recursing on the assignment expression.
   * This returns a list of attributes that are used in the Instrumentation statement.
   * @param expressionStatement
   * @return
   */
  def assignmentInstrumHelper(expressionStatement: ExpressionStatement) = {
   val attributes: List[Attribute] = ExpressionUtils.recurseExpression(expressionStatement.getExpression)
   attributes
  }

}
