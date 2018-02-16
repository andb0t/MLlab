package mllab


object Mllab {
  def main(args: Array[String]): Unit = {
      println("This is my entry point for MLlab!")
      val reader = new Reader("src/test/resources/test.csv", label=3)
      reader.loadFile()
      val X = reader.getX()
      val y = reader.getY()
      val clf = new Classifier("Mean")
      clf.train(X, y)
      println("Check prediction on training set")
      clf.predict(X)
  }
}
