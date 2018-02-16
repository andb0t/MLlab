package mllab

import scala.collection.mutable.ListBuffer


class RandomClassifier(verbose: Int = 1) {

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


class NNClassifier(verbose: Int = 1) {

  var X_NN = new ListBuffer[List[Float]]()
  var y_NN = new ListBuffer[Int]()

  def train(X: List[List[Float]], y: List[Int]): Unit = {
    assert (X.length == y.length)
    if (verbose > 0) println("No training necessary")
    X.copyToBuffer(X_NN)
    y.copyToBuffer(y_NN)
  }

  def predict(X: List[List[Float]]): List[Int] = {
    var result = new ListBuffer[Float]()
    for (instance <- X){
      var min_distance: Double = Double.MaxValue
      var min_i = -1
      for (i <- 0 until X_NN.length) {
        var squares = for ((x, y) <- X_NN(i) zip instance) yield Math.pow(x - y, 2)
        val distance = squares.sum
        if (distance < min_distance) {
          min_distance = distance
          min_i = i
        }
      }
      val prediction = y_NN(min_i)
      result += prediction
      if (verbose > 1) println("Result is " + prediction)
    }
    result.toList.map(_.toInt)
  }
}
