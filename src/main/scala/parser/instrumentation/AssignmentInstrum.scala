package parser.instrumentation
import org.eclipse.jdt.core.dom._
import parser.utils.{Attribute, ExpressionUtils}

class AssignmentInstrum {
  def assignmentInstrumHelper(expressionStatement: ExpressionStatement) = {
   val attributes: List[Attribute] = ExpressionUtils.recurseExpression(expressionStatement.getExpression)
   attributes
  }

}
