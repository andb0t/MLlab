package data

import scala.io.Source


/**
  * A class to read and access data files
  *
  * @constructor Create a new data file reader
  * @param fileName The file path to access (expects .csv files)
  * @param label The instance label column (if present)
  * @param index The index column (if present)
  * @param sep The column separator
  */
class Reader(val fileName: String, val label: Int = -1, val index: Int = -1, sep: String = " ") {

  /** The object to collect the data */
  val data = new Data(index)
  /** The number of columns in the data file */
  var nColumns: Int = 0

  /** Load the data file */
  def loadFile(): Unit = {
    println("Load the file " + fileName)
    val sourceStream = Source.fromFile(fileName)
    for (line <- sourceStream.getLines().drop(1).toVector){
      val values = line.split(sep).map(_.trim).map(_.toDouble)
      implicit def arrayToList[A](values: Array[Double]) = values.toList
      data.addInstance(values)
      nColumns = values.length
    }
    sourceStream.close
  }

  /**
   * Get the list of features
   * @return List of features
   */
  def getX(): List[List[Double]] =
    if (label != -1) data.getData(not=label)
    else data.getData(not=nColumns -1)

  /**
   * Get the list of labels
   * @return List of labels
   */
  def getY(): List[Double] =
    if (label != -1) data.getData(only=label).head
    else data.getData(only=nColumns -1).head

}
