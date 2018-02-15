package mllab

object mllab {
  def main(args: Array[String]): Unit = {
      println("This is my entry point for MLlab!")
      val reader = new Reader("not_a_real_file.txt")
      reader.loadFile()
      println("This is some of the content: " + reader.content(0)(1) )
  }
}
