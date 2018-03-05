package classifiers


abstract class Classifier() {
  def train(X: List[List[Double]], y: List[Int]): Unit
  def predict(X: List[List[Double]]): List[Int]
  def diagnostics(): Map[String, List[(Double, Double)]] = {
    Map()
  }
}
