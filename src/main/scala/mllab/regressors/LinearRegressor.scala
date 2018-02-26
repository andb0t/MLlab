package regressors

import scala.collection.mutable.ListBuffer


class LinearRegressor() extends Regressor {

  def train(X: List[List[Float]], y: List[Float]): Unit =
    require(X.length == y.length, "both arguments must have the same length")

  def predict(X: List[List[Float]]): List[Float] =
    for (instance <- X) yield Math.random.toFloat

}
