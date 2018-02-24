package regressors


abstract class Regressor() {
  def train(X: List[List[Float]], y: List[Float]): Unit
  def predict(X: List[List[Float]]): List[Float]
}
