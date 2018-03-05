package data

import scala.io.Source


class Reader(val fileName: String, val label: Int = -1, val index: Int = -1) {

  val data = new Data(index)
  val sep: String = " "
  var nColumns: Int = 0

  println("Instantiating a reader!")

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

  def getX(): List[List[Double]] =
    if (label != -1) data.getData(not=label)
    else data.getData(not=nColumns -1)

  def getY(): List[Double] =
    if (label != -1) data.getData(only=label).head
    else data.getData(only=nColumns -1).head

}
