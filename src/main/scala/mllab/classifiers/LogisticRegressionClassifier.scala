package classifiers

import scala.collection.mutable.ListBuffer


class LogisticRegressionClassifier() extends Classifier {

  val name: String = "LogisticRegressionClassifier"

  def train(X: List[List[Double]], y: List[Int]): Unit = {
    require(X.length == y.length, "number of training instances and labels is not equal")
  }

  def predict(X: List[List[Double]]): List[Int] =
    for (instance <- X) yield 0

}
