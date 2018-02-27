package regressors

import scala.collection.mutable.ListBuffer


class RandomRegressor() extends Regressor {

  def train(X: List[List[Double]], y: List[Double]): Unit =
    require(X.length == y.length, "both arguments must have the same length")

  def predict(X: List[List[Double]]): List[Double] =
    for (instance <- X) yield Math.random

}
