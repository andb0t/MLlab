package mllab

import scala.io.Source
// import vegas._
// import org.apache.spark.SparkContext
// import org.apache.spark.SparkContext._
// import org.apache.spark.SparkConf


class Reader(var fileName: String, var label: Int, var index: Int = -1) {

  val data = new Data(index)
  val sep: String = " "

  println("Instantiating a reader!")

  def loadFile(): Unit = {
    println("Load the file " + fileName)

    for (line <- Source.fromFile(fileName).getLines().drop(1).toVector){
      val values = line.split(sep).map(_.trim).map(_.toFloat)
      implicit def arrayToList[A](values: Array[Float]) = values.toList
      data.addInstance(values)
    }
  }

  def getX(): List[List[Float]] = {
    data.getData(not=label)
  }

  def getY(): List[Int] = {
    data.getData(only=label)(0).map(_.toInt)
  }

  def plot(figName: String="data.png"): Unit = {
    println("Plot the dataset to " + figName)

  }
}