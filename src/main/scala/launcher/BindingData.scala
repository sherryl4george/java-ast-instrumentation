package launcher

case class BindingData(name: String, value: String, itemType: String, lineNumber: String, seenAt: String){
  override def toString: String = s"Name: $name, Type: $itemType, Value: $value, Line: $lineNumber, seenAt: $seenAt"
  def toShortString: String = s"Name: $name, Type: $itemType, Value: $value"
}
