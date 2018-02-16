package mllab

object Mllab {
  def main(args: Array[String]): Unit = {
      println("This is my entry point for MLlab!")
      val reader = new Reader("src/test/resources/test.csv")
      reader.loadFile()
      val data = reader.data.getData()
      println("This is some of the content: " + data(0)(1) )
      val analyzer = new Analyzer(data)
  }
}
