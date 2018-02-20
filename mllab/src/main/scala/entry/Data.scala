package mllab

import scala.collection.mutable.ListBuffer


class Data(index: Int = -1) {
/*
  Data class
  index: column index of an index column in the data to be ignored
*/


  var data = new ListBuffer[List[Float]]()

  def addInstance(instance: List[Float]): Unit = {
    // println("Adding to list " + instance)
    data += instance
  }

  def getData(not: Int = -1, only: Int = -1): List[List[Float]] = {
    var dataList = data.toList
    if (only != -1) {
      List(dataList.map(_.apply(only)))

    }else if (not != -1) {
      dataList = dataList.map(x => x.take(not)++:x.takeRight(x.length - not - 1))
      if (index != -1) {
        val newIndex = if (index < not) index else index - 1
        dataList.map(x => x.take(newIndex)++:x.takeRight(x.length - newIndex - 1))
      }
      else {
        dataList
      }

    }else{
      dataList
    }
  }
}
