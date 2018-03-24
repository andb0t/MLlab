package classifiers

import scala.collection.mutable.ListBuffer

import datastructures._
import evaluation._
import utils._


/** Logistic regression classifier
 * @param alpha Learning rate
 * @param tol Loss tolerance to stop training
 * @param maxIter Maximum number of training iterations
 * @param degree Order of polynomial features to add to the instances (1 for no addition)
 */
class LogisticRegressionClassifier(alpha: Double = 1, tol: Double = 0.01, maxIter: Int = 1000, degree: Int=1) extends Classifier {

  val name: String = "LogisticRegressionClassifier"

  var weight = new ListBuffer[Double]()
  var bias: Double = 0

  var lossEvolution = new ListBuffer[(Double, Double)]()

  def logistic(x: Double): Double = 1.0 / (Math.exp(-x) + 1.0)

  def getProbabilities(X: List[List[Double]]): List[Double] =
    for (instance <- X) yield logistic(Maths.dot(weight.toList, instance) + bias)

  def lossGradient(X: List[List[Double]], y: List[Int]): List[Double] = {
    // dLoss = d(LogLoss) = Sum ((p - y) * x) / nInstances
    val weightGradient: List[Double] =
      for (feature <- X.transpose) yield (
        for (pyx <- (getProbabilities(X), y.map(_.toDouble), feature).zipped.toList)
          yield (pyx._1 - pyx._2) * pyx._3
      ).sum / y.length
    val biasGradient: Double = (for (py <- getProbabilities(X) zip y) yield py._1 - py._2).sum / y.length
    biasGradient::weightGradient
  }

  def _train(X: List[List[Double]], y: List[Int]): Unit = {
    require(X.length == y.length, "number of training instances and labels is not equal")
    for (i <- 0 until X.head.length)
      weight += 0
    bias = 0

    def updateWeights(count: Int): Unit = {
      val loss: Double = Evaluation.LogLoss(getProbabilities(X), y)
      lossEvolution += Tuple2(count.toDouble, loss)
      if (loss > tol && count < maxIter) {
        val weightUpdate = lossGradient(X, y).map(_ * alpha)
        if (count % 100 == 0 || (count < 50 && count % 10 == 0) || (count < 5))
          println(s"Step $count with loss %.4e".format(loss))
        // println(s"$count. step:")
        // println(" - current log loss: %.3f".format(loss))
        // println(" - current weight " + weight + " bias " + bias)
        // println(" - weightUpdate " + weightUpdate)
        bias = bias - weightUpdate.head
        for (i <- 0 until weight.length)
          weight(i) = weight(i) - weightUpdate(i + 1)
        updateWeights(count + 1)
      }else{
        println(s"Final values after $count steps at loss %.4e:".format(loss))
        println("weight: " + weight + " bias: " + bias)
      }
    }

    updateWeights(0)
  }

  def _predict(X: List[List[Double]]): List[Int] =
    for (p <- getProbabilities(X)) yield if (p > 0.5) 1 else 0

  def predict(X: List[List[Double]]): List[Int] =
    _predict(DataTrafo.addPolyFeatures(X, degree))

  def train(X: List[List[Double]], y: List[Int]): Unit =
    _train(DataTrafo.addPolyFeatures(X, degree), y)

  override def diagnostics(): Map[String, List[(Double, Double)]] = {
    Map("loss" -> lossEvolution.toList)
  }
}
