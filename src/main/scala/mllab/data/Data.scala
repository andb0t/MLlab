package data

import scala.collection.mutable.ListBuffer


/**
  * A class to handle datasets
  *
  * @constructor Create a new dataset
  * @param index If specified: column index of an index column in the data to be ignored
  */
class Data(index: Int = -1) {

  /**
   * The object to collect the data
   */
  var data = new ListBuffer[List[Double]]()

  /**
   * Adds an instance to the data
   * @param instance List of features for this instance
   */
  def addInstance(instance: List[Double]): Unit = {
    // println("Adding to list " + instance)
    data += instance
  }

  /**
   * Get the loaded dataset
   * @param not Skip this column
   * @param only Only read this column
   * @return List of instances
   */
  def getData(not: Int = -1, only: Int = -1): List[List[Double]] = {
    def removeCol(dataList: List[List[Double]], index: List[Int]): List[List[Double]] =
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
