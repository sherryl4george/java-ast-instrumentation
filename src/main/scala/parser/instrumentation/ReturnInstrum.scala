package parser.instrumentation
import org.eclipse.jdt.core.dom._
import parser.utils.{Attribute, ExpressionUtils}

class ReturnInstrum {

  def returnInstrumHelper(statement: ReturnStatement) = {
    val attributes: List[Attribute] = ExpressionUtils.recurseExpression(statement.getExpression)
    attributes
  }
}
