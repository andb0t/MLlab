package mllab

object Mllab {
  def main(args: Array[String]): Unit = {
      println("This is my entry point for MLlab!")
      val reader = new Reader("not_a_real_file.txt")
      reader.loadFile()
      val content = reader.content.getData()
      println("This is some of the content: " + content(0)(1) )
      val analyzer = new Analyzer(content)
  }
}
