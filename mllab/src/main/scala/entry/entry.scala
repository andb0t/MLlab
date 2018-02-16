package mllab


object Mllab {
  def main(args: Array[String]): Unit = {
      println("Executing MLlab!")

      val trainReader = new Reader("src/test/resources/train.csv", label=3, index=0)
      trainReader.loadFile()
      val X_train = trainReader.getX()
      val y_train = trainReader.getY()
      // val clf = new RandomClassifier()
      // val clf = new kNNClassifier(k=5)
      val clf = new DecisionTreeClassifier(depth=2)
      clf.train(X_train, y_train)
      // println("Check prediction on training set")
      // clf.predict(X_train)

      val testReader = new Reader("src/test/resources/test.csv", label=3, index=0)
      testReader.loadFile()
      val X_test = testReader.getX()
      val y_test = testReader.getY()
      println("Now do prediction on test set")
      val y_pred = clf.predict(X_test)
      assert (y_pred.length == y_test.length)
      println("Predicted values:")
      for (i <- 0 until Math.min(y_pred.length, 10)) {
        println("Test instance " + i + ": prediction " + y_pred(i) + " true value " + y_test(i))
      }
      Evaluation.matrix(y_pred, y_test)
      println("Precision: " + Evaluation.precision(y_pred, y_test))
      println("Recall: " + Evaluation.recall(y_pred, y_test))
      println("f1: " + Evaluation.f1(y_pred, y_test))
  }
}
