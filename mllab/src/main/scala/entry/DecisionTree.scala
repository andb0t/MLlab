package mllab

import scala.collection.mutable.ListBuffer


class DecisionTreeClassifier() {

  var decisionSequence = new ListBuffer[(Int, Float)]()

  def train(X: List[List[Float]], y: List[Int]): Unit = {
    assert (X.length == y.length)
  }

  def predict(X: List[List[Float]]): List[Int] = {
    val result = for (x <- X) yield Math.random.round
    result.toList.map(_.toInt)
  }
}
