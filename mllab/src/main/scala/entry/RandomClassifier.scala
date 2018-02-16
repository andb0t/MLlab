package mllab

import scala.collection.mutable.ListBuffer


class RandomClassifier() {

  val verbose: Int = 1

  def train(X: List[List[Float]], y: List[Int]): Unit = {
    assert (X.length == y.length)
    if (verbose > 1) {
      for (i <- 0 until y.length){
        println("Instance " + i + ": " + X(i) + " has label " + y(i))
      }
    }
    if (verbose > 0) println("No training necessary")
  }

  def predict(X: List[List[Float]]): List[Int] = {
    var result = new ListBuffer[Float]()
    for (instance <- X){
      val prediction = if (Math.random < 0.5) 0 else 1
      result += prediction
      if (verbose > 1) println("Result of " + instance + " is " + prediction)
    }
    result.toList.map(_.toInt)
  }

}
