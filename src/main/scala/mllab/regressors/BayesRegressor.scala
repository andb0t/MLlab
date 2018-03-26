package regressors


/** Bayes regressor
 *
 */
class BayesRegressor() extends Regressor {

  val name: String = "BayesRegressor"

  def train(X: List[List[Double]], y: List[Double]): Unit =
    require(X.length == y.length, "both arguments must have the same length")

  def predict(X: List[List[Double]]): List[Double] =
    for (instance <- X) yield Math.random

}
