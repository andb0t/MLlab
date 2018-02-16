package mllab

import scala.collection.mutable.ListBuffer


class Data() {

  var data = new ListBuffer[List[Float]]()

  def addInstance(instance: List[Float]): Unit = {
    // println("Adding to list " + instance)
    data += instance
  }

  def getData(not: Int = -1, only: Int = -1): List[List[Float]] = {
    var dataList = data.toList
    if (only != -1) {
      return List(dataList.map(_.apply(only)))
    }
    if (not != -1) {
      return dataList.map(x => x.take(not)++:x.takeRight(x.length - not - 1))
    }
    return dataList
  }
}
