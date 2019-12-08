package parser.instrumentation

import com.typesafe.scalalogging.LazyLogging

import scala.jdk.CollectionConverters._
import org.eclipse.jdt.core.dom.{MethodDeclaration, SingleVariableDeclaration}
import parser.utils.{Attribute, ExpressionUtils}

/**
 * The MethodDeclaration Instrumentor class.
 * Identifies all method declarations.
 * This returns a list of attributes that are used in the Instrumentation statement.
 */
class MethodDeclarationInstrum  extends LazyLogging{
  /**
   * Identifies attributes for all method declarations with parameters.    *
   * @param methodDeclaration
   * @return
   */
  def methodDeclarationInstrumHelper(methodDeclaration : MethodDeclaration) = {
    val parameters: List[SingleVariableDeclaration] = methodDeclaration.parameters().asScala.toList.asInstanceOf[List[SingleVariableDeclaration]]
    var attributes : List[Attribute] = List()
    //Recurse on the parameter name for each method parameter.
    parameters.map(x=>{
      attributes = attributes ++ ExpressionUtils.recurseExpression(x.getName)
    })
    logger.info("Total attributes to be added in method declaration instrumentation - " + attributes.length)
    attributes
  }
}
