package parser.utils

/**
 * The Attribute class, that is created for every instrumentation statement to give details about the variables.
 * @param expType - Includes the type of statement, Assign, For, etc.
 * @param binding - Mentions the binding of a particular method/variable.
 * @param variable - Gives information of the variable (name and value).
 */
case class Attribute(expType: String, binding: String, variable: String) {

}
