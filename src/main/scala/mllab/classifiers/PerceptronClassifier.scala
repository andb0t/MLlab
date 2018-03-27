package classifiers

import datastructures._
import utils._


/** Perceptron classifier
 * @param alpha Learning rate
 * @param degree Order of polynomial features to add to the instances (1 for no addition)
 */
class PerceptronClassifier(alpha: Double = 1.0, degree: Int=1) extends Classifier {

  val name: String = "PerceptronClassifier"

  /** The weights with the intercept as the head element */
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

    def trainEpochs(epoch: Int, isDone: Boolean, weight: List[Double]): List[Double] = {
      val maxEpochs: Int = 1000
      if (epoch == maxEpochs || isDone) {
          println("Final% 4d with weights ".format(epoch) +
            weight.map(p => "%+.3f".format(p)).mkString(", ")
        )
        weight
      }
      else {
        if (epoch % 100 == 0 || (epoch < 50 && epoch % 10 == 0) || (epoch < 5))
          println("Epoch% 4d with weights ".format(epoch) +
            weight.map(p => "%+.3f".format(p)).mkString(", ")
        )

        def trainSteps(step: Int, damp: Int, weight: List[Double]): Tuple2[List[Double], Boolean] = {
          if (step == X.length) Tuple2(weight, true)
          else {
            val i: Int = (Math.random * X.length).toInt
            if (!isCorrect(X(i), y(i), weight)) {
              val sign: Int = if (y(i) == 1) 1 else -1  // move closer to correct classification
              val newWeight = ((1.0 :: X(i)) zip weight).map{case (x, w) => w + (alpha * sign * x) / damp}
              Tuple2(newWeight, false)
            }
            else
              trainSteps(step+1, damp, weight)
          }
        }

        val (newWeight: List[Double], newIsDone: Boolean) = trainSteps(0, epoch+1, weight)

        trainEpochs(epoch+1, newIsDone, newWeight)
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
