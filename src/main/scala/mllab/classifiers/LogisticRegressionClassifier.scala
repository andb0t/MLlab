package classifiers

import scala.collection.mutable.ListBuffer

import play.api.libs.json.JsValue

import algorithms._
import evaluation._
import json._
import utils._

/** Companion object providing default parameters */
object LogisticRegressionClassifier {
  val alpha: Double = 1
  val tol: Double = 0.01
  val maxIter: Int = 1000
  val degree: Int = 1
}

/** Logistic regression classifier
 * @param alpha Learning rate
 * @param tol Loss tolerance to stop training
 * @param maxIter Maximum number of training iterations
 * @param degree Order of polynomial features to add to the instances (1 for no addition)
 */
class LogisticRegressionClassifier(
  alpha: Double = LogisticRegressionClassifier.alpha,
  tol: Double = LogisticRegressionClassifier.tol,
  maxIter: Int = LogisticRegressionClassifier.maxIter,
  degree: Int = LogisticRegressionClassifier.degree
) extends Classifier {
  def this(json: JsValue) = {
    this(
      alpha = JsonMagic.toDouble(json, "alpha", LogisticRegressionClassifier.alpha),
      tol = JsonMagic.toDouble(json, "tol", LogisticRegressionClassifier.tol),
      maxIter = JsonMagic.toInt(json, "maxIter", LogisticRegressionClassifier.maxIter),
      degree = JsonMagic.toInt(json, "degree", LogisticRegressionClassifier.degree)
      )
  }

  val name: String = "LogisticRegressionClassifier"

  /** Weights for the linear transformation */
  var weight: List[Double] = Nil

  var lossEvolution = new ListBuffer[(Double, Double)]()

  /** Calculates probability score for each instance */
  def getProbabilities(X: List[List[Double]], weight: List[Double]): List[Double] =
    for (instance <- X) yield Maths.logistic(Maths.dot(weight, 1.0 :: instance))

  /** Calculates the gradient of the loss function for the given training data */
  def lossGradient(X: List[List[Double]], y: List[Int], weight: List[Double]): List[Double] = {
    // dLoss = d(logLoss) = Sum ((p - y) * x) / nInstances
    val weightGradient: List[Double] =
      for (feature <- X.transpose) yield (
        for (pyx <- (getProbabilities(X, weight), y.map(_.toDouble), feature).zipped.toList)
          yield (pyx._1 - pyx._2) * pyx._3
      ).sum / y.length
    val biasGradient: Double = (for (py <- getProbabilities(X, weight) zip y) yield py._1 - py._2).sum / y.length
    biasGradient::weightGradient
  }

  def _train(X: List[List[Double]], y: List[Int]): Unit = {  // scalastyle:ignore
    require(X.length == y.length, "number of training instances and labels is not equal")
    val nFeatures = X.head.length

    def findWeights(count: Int, weight: List[Double]): List[Double] = {
      val loss: Double = Evaluation.logLoss(getProbabilities(X, weight), y)
      lossEvolution += Tuple2(count.toDouble, loss)
      if (loss > tol && count < maxIter) {
        if (count % 100 == 0 || (count < 50 && count % 10 == 0) || (count < 5))
          println("Step% 4d with loss %.4e and weights ".format(count, loss) +
          weight.map(p => "%+.3f".format(p)).mkString(", ")
        )
        val weightUpdate = lossGradient(X, y, weight).map(_ * alpha)
        val newWeight = weight zip weightUpdate map(wu => wu._1 - wu._2)
        findWeights(count + 1, newWeight)
      }else{
        println(s"Final $count with loss %.4e and weights ".format(loss) +
          weight.map(p => "%+.3f".format(p)).mkString(", ")
        )
        weight
      }
    }

    weight = findWeights(0, List.fill(nFeatures + 1)(0))
  }

  def _predict(X: List[List[Double]]): List[Int] =  // scalastyle:ignore
    for (p <- getProbabilities(X, weight)) yield if (p > 0.5) 1 else 0

  def predict(X: List[List[Double]]): List[Int] =
    _predict(DataTrafo.addPolyFeatures(X, degree))

  def train(X: List[List[Double]], y: List[Int], sampleWeight: List[Double] = Nil): Unit =
    _train(DataTrafo.addPolyFeatures(X, degree), y)

  override def diagnostics(): Map[String, List[(Double, Double)]] = {
    Map("loss" -> lossEvolution.toList)
  }
}
