package parser.instrumentation

import org.eclipse.jdt.core.dom._
import parser.utils.{Attribute, ExpressionUtils, utils}

import scala.jdk.CollectionConverters._

/**
 * The Variable Declaration Statement Instrumentor class
 * Identifies the attributes needed for each variable declaration statement instrumentation.
 */
class VDSInstrum() {
  /**
   * A Variable declaration statement consists of Variable Declaration Fragments.
   * Eg - int i, j = 0; ========> This statement contains two fragments.
   * We identify the type, bindings and names for each of the fragments and provide them to the instrumentor.
   * @param statement
   * @return
   */
  def varDFragmentInstrumHelper(statement: VariableDeclarationStatement) ={

    val vdFragments:List[VariableDeclarationFragment] = statement.fragments().asScala.toList.filter(x=>x.isInstanceOf[VariableDeclaration]).map(x=>x.asInstanceOf[VariableDeclarationFragment])
    val result = vdFragments.map(x => {
      //Recurse on the name
      val attributes1: List[Attribute] = ExpressionUtils.recurseExpression(x.getName)
      //Recurse on the initializer.
      val attributes2: List[Attribute] = ExpressionUtils.recurseExpression(x.getInitializer)
      val attributes = attributes1 ::: attributes2
      val hasInitializer = attributes2.length > 0
      (attributes,hasInitializer)
    })
    result
  }
}
