package classifiers

import scala.collection.mutable.ListBuffer

import evaluation._
import utils._


class LogisticRegressionClassifier() extends Classifier {

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

  def train(X: List[List[Double]], y: List[Int]): Unit = {
    require(X.length == y.length, "number of training instances and labels is not equal")
    val alpha: Double = 1
    for (i <- 0 until X.head.length)
      weight += 0
    bias = 0

    def updateWeights(count: Int): Unit = {
      val loss = Evaluation.LogLoss(getProbabilities(X), y)
      lossEvolution += Tuple2(count.toDouble, loss)
      if (loss > 0.01 && count < 1000) {
        val weightUpdate = lossGradient(X, y).map(_ * alpha)
        // println(s"$count. Step with loss $loss:")
        // println(" - current log loss: %.3f".format(loss))
        // println(" - current weight " + weight + " bias " + bias)
        // println(" - weightUpdate " + weightUpdate)
        bias = bias - weightUpdate.head
        for (i <- 0 until weight.length)
          weight(i) = weight(i) - weightUpdate(i + 1)
        updateWeights(count + 1)
      }else{
        println(s"Final values after $count steps at loss %.3f:".format(loss))
        println("weight: " + weight + " bias: " + bias)
      }
    }

    updateWeights(0)
  }

  def predict(X: List[List[Double]]): List[Int] =
    for (p <- getProbabilities(X)) yield if (p > 0.5) 1 else 0

  override def diagnostics(): Map[String, List[(Double, Double)]] = {
    Map("loss" -> lossEvolution.toList)
  }
}
