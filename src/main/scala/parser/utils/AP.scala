package parser.utils

case class AP(expType: String, binding: String, variable: String) {
  def this(expType: String, binding: String, variable: Int) = this(expType, binding, variable.toString)
}
