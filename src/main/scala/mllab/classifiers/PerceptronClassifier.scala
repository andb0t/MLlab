package classifiers

import scala.collection.mutable.ListBuffer
import scala.util.control.Breaks._

import datastructures._
import utils._


/** Perceptron classifier
 * @param alpha Learning rate
 * @param degree Order of polynomial features to add to the instances (1 for no addition)
 */
class PerceptronClassifier(alpha: Double = 1.0, degree: Int=1) extends Classifier {

  val name: String = "PerceptronClassifier"

  var weight = new ListBuffer[Double]()
  var bias: Double = 0

  /** Calculates the distance to the decision decision hyperplane */
  def distToPlane(instance: List[Double]): Double = {
    val side: Double = bias + Maths.dot(weight.toList, instance)
    side / Maths.abs(weight.toList)
  }

  /** Gets the prediction for this instance
    *
    * Predicts if instance is above or below the decision hyperplane
    */
  def getPrediction(instance: List[Double]): Int =
    if (distToPlane(instance) > 0) 1 else 0

  def isCorrect(instance: List[Double], label: Int): Boolean =
    getPrediction(instance) == label

  def _train(X: List[List[Double]], y: List[Int]): Unit = {
    require(X.length == y.length, "number of training instances and labels is not equal")

    // initialize parameters
    bias  = 0
    for (i <- 0 until X.head.length) {
      weight += 0  // replace by Math.random?
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
            // take random instance to avoid being trapped between two too close instances
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

  def _predict(X: List[List[Double]]): List[Int] =
    for (instance <- X) yield getPrediction(instance)

  def predict(X: List[List[Double]]): List[Int] =
    _predict(DataTrafo.addPolyFeatures(X, degree))

  def train(X: List[List[Double]], y: List[Int]): Unit =
    _train(DataTrafo.addPolyFeatures(X, degree), y)

}
