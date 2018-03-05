package classifiers

import scala.collection.mutable.ListBuffer


class RandomClassifier() extends Classifier {

  val name: String = "RandomClassifier"

  def train(X: List[List[Double]], y: List[Int]): Unit =
    require(X.length == y.length, "number of training instances and labels is not equal")

  def predict(X: List[List[Double]]): List[Int] =
    for (instance <- X) yield if (Math.random < 0.5) 0 else 1

}
