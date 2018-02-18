package mllab

import scala.collection.mutable.ListBuffer


class SVMClassifier() {

  var weight = new ListBuffer[Double]()
  var bias: Double = 0

  def dot(a: List[Double], b: List[Double]): Double = {
    assert (a.length == b.length)
    var prod: Double = 0
    for (i <- 0 until a.length) {
      prod += a(i) * b(i)
    }
    return prod
  }

  def train(X: List[List[Float]], y: List[Int]): Unit = {
    assert (X.length == y.length)
    weight += 1
    weight += 0
    bias = 0
  }

  def predict(X: List[List[Float]]): List[Int] = {
    println("This is still a placeholder")
    var result = new ListBuffer[Float]()
    for (instance <- X){
      val side = bias + dot(weight.toList, instance.map(_.toDouble))
      val prediction = if (side > 0) 1 else 0
      result += prediction
    }
    result.toList.map(_.toInt)
  }

}
