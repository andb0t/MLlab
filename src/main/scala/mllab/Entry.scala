package mllab

import classifiers._
import data._
import evaluation._
import plotting._
import regressors._


object Mllab {
  def main(args: Array[String]): Unit = {
    println("Execute MLlab!")

    val task =
      if (args.length == 0 || args(0) == "clf") "clf"
      else if (args(0) == "reg") "reg"
      else throw new IllegalArgumentException("task not implemented. Chose 'clf' or 'reg'")

    if (task == "clf") {
      println{"Train the model"}
      val trainReader = new Reader("src/test/resources/train_clf.csv", label=3, index=0)
      trainReader.loadFile()
      val X_train = trainReader.getX()
      val y_train = trainReader.getY().map(_.toInt)

      val clf =
        if (args.length <= 1 || args(1) == "Random") new RandomClassifier()
        else if (args(1) == "kNN") new kNNClassifier(k=3)
        else if (args(1) == "DecisionTree") new DecisionTreeClassifier(depth=3)
        else if (args(1) == "Perceptron") new PerceptronClassifier(alpha=1)
        else if (args(1) == "NeuralNetwork") new NeuralNetworkClassifier(alpha=0.01, activation= "tanh", layers=List(2, 10, 10, 2), regularization=0.05)
        else if (args(1) == "SVM") new SVMClassifier()
        else if (args(1) == "LogisticRegression") new LogisticRegressionClassifier(alpha=1, maxIter=2000)
        else throw new IllegalArgumentException("algorithm not implemented.")
      clf.train(X_train, y_train)

      // println("Check prediction on training set")
      // clf.predict(X_train)

      println{"Apply to test set"}
      val testReader = new Reader("src/test/resources/test_clf.csv", label=3, index=0)
      testReader.loadFile()
      val X_test = testReader.getX()
      val y_test = testReader.getY().map(_.toInt)

      println("Now do prediction on test set")
      val y_pred = clf.predict(X_test)
      assert (y_pred.length == y_test.length)
      println("Predicted values:")
      // for (i <- 0 until Math.min(y_pred.length, 10)) {
      //   println("Test instance " + i + ": prediction " + y_pred(i) + " true value " + y_test(i))
      // }

      println("Evaulate of the model")
      Evaluation.matrix(y_pred, y_test)
      println("Precision: %.2f".format(Evaluation.precision(y_pred, y_test)))
      println("Recall: %.2f".format(Evaluation.recall(y_pred, y_test)))
      println("f1: %.2f".format(Evaluation.f1(y_pred, y_test)))

      println("Visualize the data")
      Plotting.plotData(X_train, y_train)
      Plotting.plotClf(X_train, y_train, clf)
      Plotting.plotGrid(X_train, clf)

      val mDiag = clf.diagnostics
      Plotting.plotCurves(List(mDiag.getOrElse("loss", List((0.0, 0.0)))), List("loss"), name= "plots/loss.pdf")
      Plotting.plotCurves(List(mDiag.getOrElse("alpha", List((0.0, 0.0)))), List("alpha"), name= "plots/alpha.pdf")

    }
    else if (task == "reg") {
      println("\n\nTry basic regressor functionality")

      val trainReader = new Reader("src/test/resources/train_reg.csv", label= -1, index=0)
      trainReader.loadFile()
      val X_train = trainReader.getX()
      val y_train = trainReader.getY()

      val testReader = new Reader("src/test/resources/test_reg.csv", label= -1, index=0)
      testReader.loadFile()
      val X_test = testReader.getX()
      val y_test = testReader.getY()

      println("Test feature vector: " + X_train.head + " with label " + y_train.head)

      val reg =
        if (args.length <= 1 || args(1) == "Random") new RandomRegressor()
        else if (args(1) == "Linear") new LinearRegressor()
        else throw new IllegalArgumentException("algorithm not implemented.")
      reg.train(X_train, y_train)

      val y_pred: List[Double] = reg.predict(X_test)
      for (i <- 0 until Math.min(y_pred.length, 10)) {
        println("Test instance " + i + ": " + X_test(i) + " prediction %.2f  true value %.2f".format(y_pred(i), y_test(i)))
      }

      println("Visualize the data")
      Plotting.plotRegData(X_train, y_train)
    }
  }
}
