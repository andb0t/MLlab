package regressors

import scala.collection.mutable.ListBuffer

import utils._


class LinearRegressor() extends Regressor {

  var weight = new ListBuffer[Double]()
  var bias: Double = 0

  def train(X: List[List[Float]], y: List[Float]): Unit = {
    require(X.length == y.length, "both arguments must have the same length")
    for (i <- 0 until X.head.length)
      weight += 1
    bias = 0
  }

  def predict(X: List[List[Float]]): List[Float] =
    (for (instance <- X) yield Maths.dot(weight.toList, instance.map(_.toDouble)) + bias).map(_.toFloat)

}
