package regressors

import scala.collection.mutable.ListBuffer

import evaluation._
import utils._


class LinearRegressor() extends Regressor {

  var weight = new ListBuffer[Double]()
  var bias: Double = 0

  def lossGradient(X: List[List[Double]], y: List[Double]): List[Double] = {
    // dLoss = d(MSE scaled) = Sum (const * linearDistanceScaled * instanceVector)
    val linDist: List[Double] = for (xy <- X zip y) yield (Maths.dot(weight.toList, xy._1) + bias - xy._2) / Math.abs(y.sum)
    val weightGradient: List[Double] = (X zip linDist).map{case (x, d) => x.map(_ * d)}.reduce(Maths.plus(_, _))
    val biasGradient: Double = linDist.sum
    biasGradient::weightGradient
  }

  def train(X: List[List[Double]], y: List[Double]): Unit = {
    val alpha: Double = 0.1
    require(X.length == y.length, "both arguments must have the same length")
    for (i <- 0 until X.head.length)
      weight += 0
    bias = 0

    def updateWeights(count: Int): Unit = {
      val scaleIndependentLoss = Evaluation.MSES(predict(X), y)
      if (scaleIndependentLoss > 0.0001) {
        val weightUpdate = lossGradient(X, y).map(_ * alpha)
        // println(s"$count. Step with loss $scaleIndependentLoss:")
        // println(" - current MSE: " + Evaluation.MSE(predict(X), y))
        // println(" - current MSES: " + Evaluation.MSES(predict(X), y))
        // println(" - current weight " + weight + " bias " + bias)
        // println(" - lossGradient " + lossGradient(X, y))
        // println(" - weightUpdate " + weightUpdate)
        bias = bias - weightUpdate.head
        for (i <- 0 until weight.length)
          weight(i) = weight(i) - weightUpdate(i + 1)
        updateWeights(count + 1)
      } else{
        println(s"Final values after $count steps at scaled loss $scaleIndependentLoss:")
        println("weight: " + weight + " bias: " + bias)
      }
    }

    updateWeights(0)
  }

  def predict(X: List[List[Double]]): List[Double] =
    for (instance <- X) yield Maths.dot(weight.toList, instance) + bias

}
