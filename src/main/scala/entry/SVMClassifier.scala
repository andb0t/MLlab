package mllab

import scala.collection.mutable.ListBuffer
import scala.util.control.Breaks._


class SVMClassifier(alpha: Double = 1) {

  var weight = new ListBuffer[Double]()
  var bias: Double = 0

  def getDistance(instance: List[Float]): Double = {
    val side: Double = bias + Maths.dot(weight.toList, instance.map(_.toDouble))
    side / Maths.abs(weight.toList)
  }

  def getPrediction(instance: List[Float]): Int = {
    if (getDistance(instance) > 0) 1 else 0
  }

  def isCorrect(instance: List[Float], label: Int): Boolean = {
    getPrediction(instance) == label
  }

  def train(X: List[List[Float]], y: List[Int]): Unit = {
    assert (X.length == y.length)
    // initialize parameters
    bias  = 0
    for (i <- 0 until X.head.length) {
      weight += 0  // TODO: replace by Math.random
    }

    // loop over training set until all correctly classified
    var needMoreTraining: Boolean = true
    var epoch: Int = 0
    val MaxEpochs: Int = 1000
    breakable{
      while(needMoreTraining) {
        if (epoch > MaxEpochs) {
          println("Maximum number of loops " + MaxEpochs + " reached! Abort training!")
          break
        }
        println(epoch + ". new training loop with weights " + weight + " and bias " + bias)
        assert (weight.length == X.head.length)
        epoch += 1
        needMoreTraining = false
        breakable{
          for (idx <- 0 until X.length) {
            // take random instance to avoid being trapped in two close instances
            val i: Int = (Math.random * X.length).toInt
            if (!isCorrect(X(i), y(i))) {
              println("Incorrect classification of instance " + i + ": " + X(i) + " true label " + y(i))
              val sign: Int = if (y(i) == 1) 1 else -1
              bias = bias + (alpha * sign) / epoch
              for (j <- 0 until weight.length) {
                weight(j) = weight(j) + (alpha * sign * X(i)(j)) / epoch
              }
              needMoreTraining = true
              break
            }
          }
        }
      }
    }
    println("Training finished after " + epoch + " epochs!")
    println("Weight: " + weight)
    println("Bias: " + bias)
  }

  def predict(X: List[List[Float]]): List[Int] = {
    println("This is still a placeholder")
    var result = new ListBuffer[Float]()
    for (instance <- X){
      val prediction = getPrediction(instance)
      result += prediction
    }
    result.toList.map(_.toInt)
  }

}
