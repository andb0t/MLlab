package mllab

import org.rogach.scallop._

import classifiers._
import clustering._
import data._
import evaluation._
import json._
import plotting._
import regressors._


/** The current entry point for MLlab
 *
 * Currently, this is a collection of usage examples
 *
 * @todo generalize to allow simple integration in other projects
 */
object Mllab {

  /** Parses the input arguments */
  class Conf(arguments: Seq[String]) extends ScallopConf(arguments) {
    val clf = opt[String](
      default = Some(""),
      descr = "specify classification algorithm"
      // validate = (s: String) => List("", "Random", "SVM").contains(s)
    )
    val reg = opt[String](
      default = Some(""),
      descr = "specify regression algorithm"
      // validate = (s: String) => List("", "kNN", "Linear", "Random").contains(s)
    )
    val clu = opt[String](
      default = Some(""),
      descr = "specify clustering algorithm"
    )
    mutuallyExclusive(clf, reg)
    mutuallyExclusive(clu, reg)
    mutuallyExclusive(clf, clu)
    val input = opt[String](
      default = Some("src/test/resources"),
      descr = "directory containing the input data"
    )
    val output = opt[String](
      default = Some("plots"),
      descr = "directory to save output"
    )
    val suffix = opt[String](
      default = Some(""),
      descr = "suffix to append on all figure names"
    )
    val format = opt[String](
      default = Some("pdf"),
      descr = "figure format",
      validate = (s: String) => List("pdf", "png").contains(s)
    )
    val hyper = opt[String](
      default = Some(""),
      descr = "hyperparameters to pass to the algorithm"
    )
    val noplots = opt[Boolean](
      default = Some(false),
      descr = "do not produce plots"
    )
    verify()
  }

  /** Runs the algorithms */
  def main(args: Array[String]): Unit = {
    val conf = new Conf(args)

    println("Execute MLlab!")
    val json = JsonMagic.jsonify(conf.hyper())
    val suff = if (conf.suffix() != "") "_" + conf.suffix() else ""

    if (!conf.clu().isEmpty) {
      println("Train the clustering")

      val trainReader = new Reader(conf.input() + "/clu_train.csv", label= -1, index=0)
      trainReader.loadFile()
      val X_train = trainReader.getX()

      val testReader = new Reader(conf.input() + "/clu_test.csv", label= -1, index=0)
      testReader.loadFile()
      val X_test = testReader.getX()

      val clu =
        if (conf.clu().isEmpty || conf.clu() == "Random") new RandomClustering()
        else if (conf.clu() == "kMeans") new kMeansClustering(json)
        else if (conf.clu() == "SelfOrganizingMap") new SelfOrganizingMapClustering(json)
        else throw new IllegalArgumentException("algorithm " + conf.clu() + " not implemented.")
      clu.train(X_train)

      val y_pred = clu.predict(X_test)

      if (!conf.noplots()) {
        println("Visualize the data")
        Plotting.plotClu(X_test, y_pred, clu, name= conf.output() + "/clu_" + conf.clu() + "_clu_test" + suff + "." + conf.format())
        Plotting.plotClu(X_test, y_pred, clu, drawCentroids = true, name= conf.output() + "/clu_" + conf.clu() + "_centroids" + suff + "." + conf.format())

        for (diag <- clu.diagnostics)
          Plotting.plotCurves(List(diag._2), List(diag._1), name= conf.output() + "/clu_" + conf.clf() + "_" + diag._1 + "" + suff + "." + conf.format())
      }
    }
    else if (!conf.clf().isEmpty) {
      println("Train the classifier")

      val trainReader = new Reader(conf.input() + "/clf_train.csv", label= -1, index=0)
      trainReader.loadFile()
      val X_train = trainReader.getX()
      val y_train = trainReader.getY().map(_.toInt)

      val testReader = new Reader(conf.input() + "/clf_test.csv", label= -1, index=0)
      testReader.loadFile()
      val X_test = testReader.getX()
      val y_test = testReader.getY().map(_.toInt)

      val clf =
        if (conf.clf().isEmpty || conf.clf() == "Random") new RandomClassifier()
        else if (conf.clf() == "kNN") new kNNClassifier(json)
        else if (conf.clf() == "DecisionTree") new DecisionTreeClassifier(json)
        else if (conf.clf() == "BoostedDecisionTree") new BoostedDecisionTreeClassifier(json)
        else if (conf.clf() == "Perceptron") new PerceptronClassifier(json)
        else if (conf.clf() == "NeuralNetwork") new NeuralNetworkClassifier(json)
        else if (conf.clf() == "LogisticRegression") new LogisticRegressionClassifier(json)
        else if (conf.clf() == "NaiveBayes") new NaiveBayesClassifier(json)
        else if (conf.clf() == "SVM") new SVMClassifier()
        else throw new IllegalArgumentException("algorithm " + conf.clf() + " not implemented.")
      clf.train(X_train, y_train)

      // println("Check prediction on training set")
      // clf.predict(X_train)

      println{"Apply to test set"}

      println("Now do prediction on test set")
      val y_pred = clf.predict(X_test)
      assert (y_pred.length == y_test.length)
      // println("Predicted values:")
      // for (i <- 0 until Math.min(y_pred.length, 10)) {
      //   println("Test instance " + i + ": prediction " + y_pred(i) + " true value " + y_test(i))
      // }

      println("Evaluate the model")
      Evaluation.matrix(y_pred, y_test)
      println("Precision: %.2f".format(Evaluation.precision(y_pred, y_test)))
      println("Recall: %.2f".format(Evaluation.recall(y_pred, y_test)))
      println("f1: %.2f".format(Evaluation.f1(y_pred, y_test)))

      if (!conf.noplots()) {
        println("Visualize the data")
        Plotting.plotClfData(X_train, y_train, name= conf.output() + "/clf_" + conf.clf() + "_data" + suff + "." + conf.format())
        Plotting.plotClf(X_train, y_train, clf, name= conf.output() + "/clf_" + conf.clf() + "_clf_train" + suff + "." + conf.format())
        Plotting.plotClf(X_test, y_test, clf, name= conf.output() + "/clf_" + conf.clf() + "_clf_test" + suff + "." + conf.format())
        Plotting.plotClfGrid(X_train, clf, name= conf.output() + "/clf_" + conf.clf() + "_grid" + suff + "." + conf.format())

        for (diag <- clf.diagnostics)
          Plotting.plotCurves(List(diag._2), List(diag._1), name= conf.output() + "/clf_" + conf.clf() + "_" + diag._1 + "" + suff + "." + conf.format())
      }
    }
    else {
      println("Train the regressor")

      val trainReader = new Reader(conf.input() + "/reg_train.csv", label= -1, index=0)
      trainReader.loadFile()
      val X_train = trainReader.getX()
      val y_train = trainReader.getY()

      val testReader = new Reader(conf.input() + "/reg_test.csv", label= -1, index=0)
      testReader.loadFile()
      val X_test = testReader.getX()
      val y_test = testReader.getY()

      val reg =
        if (conf.reg().isEmpty || conf.reg() == "Random") new RandomRegressor()
        else if (conf.reg() == "Linear") new LinearRegressor(json)
        else if (conf.reg() == "DecisionTree") new DecisionTreeRegressor(json)
        else if (conf.reg() == "Bayes") new BayesRegressor(json)
        else if (conf.reg() == "kNN") new kNNRegressor(json)
        else if (conf.reg() == "NeuralNetwork") new NeuralNetworkRegressor(json)
        else throw new IllegalArgumentException("algorithm " + conf.reg() + " not implemented.")
      reg.train(X_train, y_train)

      println{"Apply to test set"}
      val y_pred: List[Double] = reg.predict(X_test)

      println("Evaluate the model")
      println("Mean Squared Error (MSE): %.2f".format(Evaluation.MSE(y_pred, y_test)))
      println("Mean Asolute Error (MAE): %.2f".format(Evaluation.MAE(y_pred, y_test)))
      println("Median Asolute Error (MAE): %.2f".format(Evaluation.MedAE(y_pred, y_test)))
      println("Explained variance score: %.2f".format(Evaluation.explainedVariance(y_pred, y_test)))
      println("R squared score: %.2f".format(Evaluation.RSqared(y_pred, y_test)))
      println("Mean Squared Log Error (MSLE): %.2f".format(Evaluation.MSLE(y_pred, y_test)))

      if (!conf.noplots()) {
        println("Visualize the data")
        Plotting.plotRegData(X_train, y_train, name= conf.output() + "/reg_" + conf.reg() + "_data" + suff + "." + conf.format())
        Plotting.plotReg(X_train, y_train, reg, name= conf.output() + "/reg_" + conf.reg() + "_reg_train" + suff + "." + conf.format())
        Plotting.plotReg(X_test, y_test, reg, name= conf.output() + "/reg_" + conf.reg() + "_reg_test" + suff + "." + conf.format())

        for (diag <- reg.diagnostics)
          Plotting.plotCurves(List(diag._2), List(diag._1), name= conf.output() + "/reg_" + conf.reg() + "_" + diag._1 + "" + suff + "." + conf.format())
      }
    }
  }
}
