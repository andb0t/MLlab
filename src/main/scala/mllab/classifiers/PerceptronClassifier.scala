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

  var weight: List[Double] = Nil

  /** Calculates the distance to the decision decision hyperplane */
  def distToPlane(instance: List[Double], weight: List[Double]): Double = {
    val side: Double = Maths.dot(weight, 1 :: instance)
    side / Maths.abs(weight.tail)
  }

  /** Gets the prediction for this instance
    *
    * Predicts if instance is above or below the decision hyperplane
    */
  def getPrediction(instance: List[Double], weight: List[Double]): Int =
    if (distToPlane(instance, weight) > 0) 1 else 0

  /** Determines if an instance is classified correctly */
  def isCorrect(instance: List[Double], label: Int, weight: List[Double]): Boolean =
    getPrediction(instance, weight) == label

  def _train(X: List[List[Double]], y: List[Int]): Unit = {
    require(X.length == y.length, "number of training instances and labels is not equal")
    val nFeatures = X.head.length

    def trainEpochs(count: Int, isDone: Boolean, weight: List[Double]): List[Double] = {
      val maxEpochs: Int = 1000
      if (count == maxEpochs || isDone) {
          println("Final% 4d with weights ".format(count) +
            weight.map(p => "%+.3f".format(p)).mkString(", ")
        )
        weight
      }
      else {
        if (count % 100 == 0 || (count < 50 && count % 10 == 0) || (count < 5))
          println("Epoch% 4d with weights ".format(count) +
            weight.map(p => "%+.3f".format(p)).mkString(", ")
        )

        var newBias = weight.head
        var newWeightVec = weight.tail
        var newIsDone = true
        breakable{
          for (idx <- 0 until X.length) {
            // take random instance to avoid being trapped between two too close instances
            val i: Int = (Math.random * X.length).toInt
            // println(s" - classify instance $idx: " + X(i) + " true label " + y(i))
            if (!isCorrect(X(i), y(i), weight)) {
              // println("Incorrect classification of instance " + i + ": " + X(i) + " true label " + y(i))
              val sign: Int = if (y(i) == 1) 1 else -1
              newBias = newBias + (alpha * sign) / (count+1)
              newWeightVec = (for (j <- 0 until newWeightVec.length) yield newWeightVec(j) + (alpha * sign * X(i)(j)) / (count+1)).toList
              newIsDone = false
              break
            }
          }
        }
        val newWeight = newBias :: newWeightVec
        trainEpochs(count+1, newIsDone, newWeight)
      }
    }

    val trainedVals = trainEpochs(0, false, 0.0 :: List.fill(nFeatures)(0.0))
    println("New trained values: " + trainedVals)

    weight = trainedVals
  }

  def _predict(X: List[List[Double]]): List[Int] =
    for (instance <- X) yield getPrediction(instance, weight)

  def predict(X: List[List[Double]]): List[Int] =
    _predict(DataTrafo.addPolyFeatures(X, degree))

  def train(X: List[List[Double]], y: List[Int]): Unit =
    _train(DataTrafo.addPolyFeatures(X, degree), y)

}
