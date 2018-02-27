package regressors


abstract class Regressor() {
  def train(X: List[List[Double]], y: List[Double]): Unit
  def predict(X: List[List[Double]]): List[Double]
}
