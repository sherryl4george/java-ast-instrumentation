package parser.instrumentation

import scala.jdk.CollectionConverters._
import org.eclipse.jdt.core.dom.{MethodDeclaration, SingleVariableDeclaration}
import parser.utils.{Attribute, ExpressionUtils}

class MethodDeclarationInstrum {
  def methodDeclarationInstrumHelper(methodDeclaration : MethodDeclaration) = {
    val parameters: List[SingleVariableDeclaration] = methodDeclaration.parameters().asScala.toList.asInstanceOf[List[SingleVariableDeclaration]]
    var attributes : List[Attribute] = List()
    parameters.map(x=>{
      attributes = attributes ++ ExpressionUtils.recurseExpression(x.getName)
    })
    attributes
  }
}
