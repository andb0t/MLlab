package classifiers

import play.api.libs.json.JsValue

import evaluation._
import json._
import utils._


/** Companion object providing default parameters */
object BoostedDecisionTreeClassifier {
  val depth: Int = 3
  val criterion: String="gini"
  val minSamplesSplit: Int = 2
  val nEstimators: Int = 3
  val boosting: String = "AdaBoost"
  val eta: Double = 1.0
  val verbose: Int = 1
}

/** Decision tree classifier
 * @param depth Depth of the tree
 * @param criterion Function to measure the quality of a split
 * @param minSamplesSplit Minimum number of samples required to split an internal node
 * @param nEstimators Number of boosting steps
 * @param boosting Boosting algorithm to use
 * @param eta Learning rate for boosting
 * @param verbose Verbosity of output
 */
class BoostedDecisionTreeClassifier(
  depth: Int = BoostedDecisionTreeClassifier.depth,
  criterion: String = BoostedDecisionTreeClassifier.criterion,
  minSamplesSplit: Int =  BoostedDecisionTreeClassifier.minSamplesSplit,
  nEstimators: Int =  BoostedDecisionTreeClassifier.nEstimators,
  boosting: String =  BoostedDecisionTreeClassifier.boosting,
  eta: Double =  BoostedDecisionTreeClassifier.eta,
  verbose: Int =  BoostedDecisionTreeClassifier.verbose
) extends Classifier {
  def this(json: JsValue) = {
    this(
      depth = JsonMagic.toInt(json, "depth", BoostedDecisionTreeClassifier.depth),
      criterion = JsonMagic.toString(json, "criterion", BoostedDecisionTreeClassifier.criterion),
      minSamplesSplit = JsonMagic.toInt(json, "minSamplesSplit", BoostedDecisionTreeClassifier.minSamplesSplit),
      nEstimators = JsonMagic.toInt(json, "nEstimators", BoostedDecisionTreeClassifier.nEstimators),
      boosting = JsonMagic.toString(json, "boosting", BoostedDecisionTreeClassifier.boosting),
      eta = JsonMagic.toDouble(json, "eta", BoostedDecisionTreeClassifier.eta),
      verbose = JsonMagic.toInt(json, "verbose", BoostedDecisionTreeClassifier.verbose)
      )
  }

  val name: String = "BoostedDecisionTreeClassifier"

  println(s"Initializing $nEstimators decision trees ...")
  val trees = List.fill(nEstimators)(
    new DecisionTreeClassifier(
      depth = depth,
      criterion = criterion,
      minSamplesSplit = minSamplesSplit,
      verbose = math.max(0, verbose - 1)
    )
  )

  def train(X: List[List[Double]], y: List[Int], sampleWeight: List[Double] = Nil): Unit = {

    def boost(trees: List[DecisionTreeClassifier], currentSampleWeight: List[Double], step: Int = 0): List[Double] = {
      if (trees.isEmpty) currentSampleWeight
      else {
        println(s"Train tree $step")
        trees.head.train(X, y, currentSampleWeight)
        val y_pred = trees.head.predict(X)
        val isCorrect: List[Boolean] = (y_pred zip y).map{case (p, t) => p == t}
        val weightSum = currentSampleWeight.sum

        val nTrue: Int = isCorrect count (_ == true)
        val nFalse: Int = isCorrect count (_ == false)
        val wTrue: Double = (isCorrect zip currentSampleWeight).filter(_._1).map(_._2).sum
        val wFalse: Double = (isCorrect zip currentSampleWeight).filter(!_._1).map(_._2).sum
        println(" - hits: %d (weighted: %.2f)".format(nTrue, wTrue))
        println(" - misses: %d (weighted: %.2f)".format(nFalse, wFalse))
        // println(" - confusion matrix")
        // Evaluation.matrix(y_pred, y, percentage = false)

        val wErrorRate = wFalse / weightSum
        println(" - weighted error rate: %.3f".format(wErrorRate))

        val learnerWeight =
          if (boosting == "AdaBoost") eta * Math.log((1.0 - wErrorRate) / wErrorRate)
          else if (boosting == "double") eta * Evaluation.f1(y_pred, y)
          else throw new NotImplementedError(s"boosting keyword $boosting not implemented")
        trees.head.decisionTree.weight =learnerWeight
        println(" - learner weight: %.3f".format(learnerWeight))

        val weightUpdate: Double =
          if (boosting == "AdaBoost") Math.exp(learnerWeight)
          else if (boosting == "double") 2.0
          else throw new NotImplementedError(s"boosting keyword $boosting not implemented")
        println(" - instance weight update: %.3f".format(weightUpdate))
        val newSampleWeights = (isCorrect zip currentSampleWeight).map{case (c, w) => if (c) w else w * weightUpdate}
        val newWeightSum = newSampleWeights.sum
        val newNormSampleWeight = newSampleWeights map (_ * weightSum / newWeightSum)
        require(Maths.round(newNormSampleWeight.sum, 6) == Maths.round(weightSum, 6), "weight conservation violated")

        boost(trees.tail, newNormSampleWeight, step + 1)
      }
    }

    val startSampleWeight =
      if (sampleWeight.isEmpty) List.fill(y.length)(1.0 / y.length)
      else sampleWeight

    boost(trees, startSampleWeight)

    println("Finished boosting!")

  }

  def predict(X: List[List[Double]]): List[Int] = {
    val treeWeights = trees.map(_.decisionTree.weight)
    val relWeights = treeWeights.map(_ / treeWeights.sum)

    def maxVote(x: List[Double]): Int = {
      val grouped =
        (for ((tree, weight) <- (trees zip relWeights)) yield (tree.predict(List(x)), weight))
        .groupBy(_._1)
        .mapValues(_.map(_._2).sum)
      grouped.maxBy(_._2)._1.head
    }

    X.map(maxVote(_))
  }

}
