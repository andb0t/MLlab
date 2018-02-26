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
    def removeCol(dataList: List[List[Float]], index: List[Int]): List[List[Float]] =
      if (index.isEmpty) dataList
      else {
        val thisIdx = index.head
        val newIndices = index.tail.map(x => if (x < thisIdx) x else x - 1)
        removeCol(dataList.map(x => x.take(thisIdx)++:x.takeRight(x.length - thisIdx - 1)), newIndices)
      }

    if (only != -1) List(data.toList.map(_.apply(only)))
    else if (not != -1 && index == -1) removeCol(data.toList, List(not))
    else if (not != -1 && index != -1) removeCol(data.toList, List(not, index))
    else data.toList
  }

}
