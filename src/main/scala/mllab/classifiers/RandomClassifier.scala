package classifiers

import scala.collection.mutable.ListBuffer


class RandomClassifier() extends Classifier {

  def train(X: List[List[Float]], y: List[Int]): Unit =
    require(X.length == y.length, "both arguments must have the same length")

  def predict(X: List[List[Float]]): List[Int] =
    for (instance <- X) yield if (Math.random < 0.5) 0 else 1

}
