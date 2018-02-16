package mllab

import scala.collection.mutable.ListBuffer


class Classifier(strat: String) {

  var result = new ListBuffer[Float]()

  def train(X: List[List[Float]], y: List[Int]): Unit = {
    assert (X.length == y.length)
    for (i <- 0 until y.length){
      println("Instance " + X(i) + " has label " + y(i))
    }
    if (strat == "Mean") {
      println("No training necessary")
    }
  }

  def predict(X: List[List[Float]]): List[Int] = {
    if (strat == "Mean") {
      for (instance <- X){
        val prediction: Float = instance.reduce(_ + _) / instance.length
        println("Result is " + prediction)
        result += prediction.toInt
      }
    }
    result.toList.map(_.toInt)
  }
}
