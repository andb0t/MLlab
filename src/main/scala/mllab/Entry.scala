package mllab

import classifiers._
import data._
import evaluation._
import plotting._
import regressors._


object Mllab {
  def parse(args: Array[String]): Map[String, String]= {
    val task =
      if (args.length == 0) "clf"
      else args(0)

    val algo =
      if (args.length <= 1) "Random"
      else args(1)

    val input =
      if (args.length <= 2) "src/test/resources"
      else args(2)

    Map("task" -> task, "algo" -> algo, "input" -> input)
  }

  def main(args: Array[String]): Unit = {
    println("Execute MLlab!")

    val argMap = parse(args)
    val task = argMap("task")
    val algo = argMap("algo")
    val input = argMap("input")

    if (task == "clf") {
      println{"Train the model"}

      val trainReader = new Reader(input + "/clf_train.csv", label=3, index=0)
      trainReader.loadFile()
      val X_train = trainReader.getX()
      val y_train = trainReader.getY().map(_.toInt)

      val testReader = new Reader(input + "/clf_test.csv", label=3, index=0)
      testReader.loadFile()
      val X_test = testReader.getX()
      val y_test = testReader.getY().map(_.toInt)

      val clf =
        if (algo == "Random") new RandomClassifier()
        else if (algo == "kNN") new kNNClassifier(k=3)
        else if (algo == "DecisionTree") new DecisionTreeClassifier(depth=3)
        else if (algo == "Perceptron") new PerceptronClassifier(alpha=1)
        else if (algo == "NeuralNetwork") new NeuralNetworkClassifier(alpha=0.01, activation= "tanh", layers=List(2, 10, 10, 2), regularization=0.05)
        else if (algo == "SVM") new SVMClassifier()
        else if (algo == "LogisticRegression") new LogisticRegressionClassifier(alpha=1, maxIter=2000)
        else throw new IllegalArgumentException("algorithm " + algo + " not implemented.")
      clf.train(X_train, y_train)

      // println("Check prediction on training set")
      // clf.predict(X_train)

      println{"Apply to test set"}

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

      val trainReader = new Reader(input + "/reg_train.csv", label= -1, index=0)
      trainReader.loadFile()
      val X_train = trainReader.getX()
      val y_train = trainReader.getY()

      val testReader = new Reader(input + "/reg_test.csv", label= -1, index=0)
      testReader.loadFile()
      val X_test = testReader.getX()
      val y_test = testReader.getY()

      println("Test feature vector: " + X_train.head + " with label " + y_train.head)

      val reg =
        if (algo == "Random") new RandomRegressor()
        else if (algo == "Linear") new LinearRegressor()
        else throw new IllegalArgumentException("algorithm " + algo + " not implemented.")
      reg.train(X_train, y_train)

      val y_pred: List[Double] = reg.predict(X_test)
      for (i <- 0 until Math.min(y_pred.length, 10)) {
        println("Test instance " + i + ": " + X_test(i) + " prediction %.2f  true value %.2f".format(y_pred(i), y_test(i)))
      }

      println("Visualize the data")
      Plotting.plotRegData(X_train, y_train)
    }
    else throw new IllegalArgumentException("task " + task + " not implemented. Chose 'clf' or 'reg'.")
  }
}
