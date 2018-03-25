package regressors

import scala.collection.mutable.ListBuffer

import datastructures._
import evaluation._
import utils._


/** Linear regressor
 * @param alpha Learning rate
 * @param tol Loss tolerance to stop training
 * @param maxIter Maximum number of training iterations
 * @param degree Order of polynomial features to add to the instances (1 for no addition)
 */
class LinearRegressor(alpha: Double = 1, tol: Double = 0.001, maxIter: Int = 1000, degree: Int=1) extends Regressor {

  val name: String = "LinearRegressor"

  var weight = new ListBuffer[Double]()
  var bias: Double = 0

  var lossEvolution = new ListBuffer[(Double, Double)]()

  /** Calculates the gradient of the loss function for the given training data */
  def lossGradient(X: List[List[Double]], y: List[Double]): List[Double] = {
    // dLoss = d(MSE scaled) = Sum (const * linearDistanceScaled * instanceVector)
    val linDist: List[Double] = for (xy <- X zip y) yield (Maths.dot(weight.toList, xy._1) + bias - xy._2) / y.length
    val weightGradient: List[Double] = (X zip linDist).map{case (x, d) => x.map(_ * d)}.reduce(Maths.plus(_, _))
    val biasGradient: Double = linDist.sum
    biasGradient::weightGradient
  }

  def _train(X: List[List[Double]], y: List[Double]): Unit = {
    require(X.length == y.length, "both arguments must have the same length")
    for (i <- 0 until X.head.length)
      weight += 0
    bias = 0

    def updateWeights(count: Int): Unit = {
      val loss = Evaluation.MSE(_predict(X), y)
      lossEvolution += Tuple2(count.toDouble, loss)
      if (loss > tol && count < maxIter) {
        val weightUpdate = lossGradient(X, y).map(_ * alpha)
        if (count % 100 == 0 || (count < 50 && count % 10 == 0) || (count < 5))
          println(s"Step $count with loss %.4e".format(loss))
        // println(s"$count. Step with loss: %.3f".format(loss))
        // println(" - current MSE: " + loss)
        // println(" - current weight " + weight + " bias " + bias)
        // println(" - weightUpdate " + weightUpdate)
        bias = bias - weightUpdate.head
        for (i <- 0 until weight.length)
          weight(i) = weight(i) - weightUpdate(i + 1)
        updateWeights(count + 1)
      } else{
        println(s"Final values after $count steps at loss %.3f:".format(loss))
        println("weight: " + weight + " bias: " + bias)
      }
    }

    updateWeights(0)
  }

  def _predict(X: List[List[Double]]): List[Double] =
    for (instance <- X) yield Maths.dot(weight.toList, instance) + bias

  def predict(X: List[List[Double]]): List[Double] =
    _predict(DataTrafo.addPolyFeatures(X, degree))

  def train(X: List[List[Double]], y: List[Double]): Unit =
    _train(DataTrafo.addPolyFeatures(X, degree), y)

  override def diagnostics(): Map[String, List[(Double, Double)]] = {
    Map("loss" -> lossEvolution.toList)
  }
}
