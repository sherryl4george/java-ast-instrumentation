package parser.instrumentation

import org.eclipse.jdt.core.dom._
import parser.utils.{Attribute, ExpressionUtils, utils}

import scala.jdk.CollectionConverters._

class VDSInstrum() {
  def varDFragmentInstrumHelper(statement: VariableDeclarationStatement) ={

    val vdFragments:List[VariableDeclarationFragment] = statement.fragments().asScala.toList.filter(x=>x.isInstanceOf[VariableDeclaration]).map(x=>x.asInstanceOf[VariableDeclarationFragment])
    val result = vdFragments.map(x => {
      val attributes1: List[Attribute] = ExpressionUtils.recurseExpression(x.getName)
      val attributes2: List[Attribute] = ExpressionUtils.recurseExpression(x.getInitializer)
      val attributes = attributes1 ::: attributes2
      val hasInitializer = attributes2.length > 0
      (attributes,hasInitializer)
    })
    result
  }
}
