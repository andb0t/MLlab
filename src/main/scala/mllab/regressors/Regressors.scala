package regressors


abstract class Regressor() {
  val name: String
  def train(X: List[List[Double]], y: List[Double]): Unit
  def predict(X: List[List[Double]]): List[Double]
}
