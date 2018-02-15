package mllab

import scala.collection.mutable.ListBuffer

class Data() {

  var data = new ListBuffer[List[Float]]()

  def addInstance(instance: List[Float]): Unit = {
    println("Adding to list " + instance)
    data += instance
  }

  def getData(): List[List[Float]] = {
    data.toList
  }
}
