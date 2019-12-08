package launcher

/**
  * Class for deserialize data recieved by the WSServer
  * Each JSON comes and is deserialized and save as an object
  * Implemented convenience methods for getting a formatted string
  * and to generate a short string
  * @param name
  * @param value
  * @param itemType
  * @param lineNumber
  * @param seenAt
  */
case class BindingData(name: String, value: String, itemType: String, lineNumber: String, seenAt: String){
  /**
    * Convenience method to get full string
    * @return
    */
  override def toString: String = s"Name: $name, Type: $itemType, Value: $value, Line: $lineNumber, seenAt: $seenAt"

  /**
    * Convenience method to get short string
    * @return
    */
  def toShortString: String = s"Name: $name, Type: $itemType, Value: $value"
}
