package mllab

import scala.collection.mutable.ListBuffer


class SVMClassifier() {

  def train(X: List[List[Float]], y: List[Int]): Unit = {
    assert (X.length == y.length)
  }

  def predict(X: List[List[Float]]): List[Int] = {
    println("This is still a placeholder")
    var result = new ListBuffer[Float]()
    for (instance <- X){
      val prediction = if (instance.sum < 0) 0 else 1
      result += prediction
    }
    result.toList.map(_.toInt)
  }

}
