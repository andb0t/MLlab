package regressors

import scala.collection.mutable.ListBuffer

import evaluation._
import utils._


class LinearRegressor(alpha: Double = 1, tol: Double = 0.001, maxIter: Int = 1000, degree: Int=1) extends Regressor {

  val name: String = "LinearRegressor"

  var weight = new ListBuffer[Double]()
  var bias: Double = 0

  var lossEvolution = new ListBuffer[(Double, Double)]()

  def addPolyFeatures(X: List[List[Double]], degree: Int=1): List[List[Double]] = {
    val xFeatures = X.transpose
    // quick hack for 1 feature
    degree  // dummy line
    val xSquaredFeature = for (x <- xFeatures.head) yield Math.pow(x, 2)
    (xSquaredFeature::xFeatures).transpose
  }

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
    _predict(addPolyFeatures(X, degree))

  def train(X: List[List[Double]], y: List[Double]): Unit =
    _train(addPolyFeatures(X, degree), y)

  override def diagnostics(): Map[String, List[(Double, Double)]] = {
    Map("loss" -> lossEvolution.toList)
  }
}
