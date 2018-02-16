package mllab

import scala.io.Source

class Reader(var fileName: String) {

  val data = new Data()
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
}
