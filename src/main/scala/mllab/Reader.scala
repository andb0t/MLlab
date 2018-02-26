package mllab

import scala.io.Source


class Reader(var fileName: String, var label: Int, var index: Int = -1) {

  val data = new Data(index)
  val sep: String = " "

  println("Instantiating a reader!")

  def loadFile(): Unit = {
    println("Load the file " + fileName)
    val sourceStream = Source.fromFile(fileName)
    for (line <- sourceStream.getLines().drop(1).toVector){
      val values = line.split(sep).map(_.trim).map(_.toFloat)
      implicit def arrayToList[A](values: Array[Float]) = values.toList
      data.addInstance(values)
    }
    sourceStream.close
  }

  def getX(): List[List[Float]] = {
    data.getData(not=label)
  }

  def getY(): List[Float] = {
    data.getData(only=label).head
  }

  def plot(figName: String="data.png"): Unit = {
    println("Plot the dataset to " + figName)
  }
}
