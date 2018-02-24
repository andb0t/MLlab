package regressors

import scala.collection.mutable.ListBuffer


class RandomRegressor() extends Regressor {

  def train(X: List[List[Float]], y: List[Float]): Unit = {
    require(X.length == y.length, "both arguments must have the same length")
  }

  def predict(X: List[List[Float]]): List[Float] = {
    var result = new ListBuffer[Float]()
    for (instance <- X){
      result += Math.random.toFloat
    }
    result.toList
  }

}
