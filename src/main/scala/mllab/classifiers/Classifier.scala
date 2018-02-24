package classifiers


abstract class Classifier() {
  def train(X: List[List[Float]], y: List[Int]): Unit
  def predict(X: List[List[Float]]): List[Int]
}
