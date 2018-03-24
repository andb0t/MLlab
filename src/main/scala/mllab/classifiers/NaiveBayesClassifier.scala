package classifiers

import scala.collection.mutable.ListBuffer


class NaiveBayesClassifier() extends Classifier {

  val name: String = "SVMClassifier"

  def train(X: List[List[Double]], y: List[Int]): Unit = {
    require(X.length == y.length, "number of training instances and labels is not equal")
    throw new NotImplementedError("SVMClassifier not implemented yet")
  }

  def predict(X: List[List[Double]]): List[Int] =
    for (instance <- X) yield 0

}
