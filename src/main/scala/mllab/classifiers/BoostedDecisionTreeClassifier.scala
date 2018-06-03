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
  val verbose: Int = 1
  val n_estimators: Int = 3
}

/** Decision tree classifier
 * @param depth Depth of the tree
 * @param criterion Function to measure the quality of a split
 * @param minSamplesSplit Minimum number of samples required to split an internal node
 * @param n_estimators Number of boosting steps
 * @param verbose Verbosity of output
 */
class BoostedDecisionTreeClassifier(
  depth: Int = BoostedDecisionTreeClassifier.depth,
  criterion: String = BoostedDecisionTreeClassifier.criterion,
  minSamplesSplit: Int =  BoostedDecisionTreeClassifier.minSamplesSplit,
  n_estimators: Int =  BoostedDecisionTreeClassifier.n_estimators,
  verbose: Int =  BoostedDecisionTreeClassifier.verbose
) extends Classifier {
  def this(json: JsValue) = {
    this(
      depth = JsonMagic.toInt(json, "depth", BoostedDecisionTreeClassifier.depth),
      criterion = JsonMagic.toString(json, "criterion", BoostedDecisionTreeClassifier.criterion),
      minSamplesSplit = JsonMagic.toInt(json, "minSamplesSplit", BoostedDecisionTreeClassifier.minSamplesSplit),
      n_estimators = JsonMagic.toInt(json, "n_estimators", BoostedDecisionTreeClassifier.n_estimators),
      verbose = JsonMagic.toInt(json, "verbose", BoostedDecisionTreeClassifier.verbose)
      )
  }

  val name: String = "BoostedDecisionTreeClassifier"

  println(s"Initializing $n_estimators decision trees ...")
  val trees = List.fill(n_estimators)(
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
        // for now, simply double correct instances' weights
        val newSampleWeights = (isCorrect zip currentSampleWeight).map{case (c, w) => if (c) w else w * 2}
        val oldWeightSum = currentSampleWeight.sum
        val newWeightSum = newSampleWeights.sum
        val newNormSampleWeight = newSampleWeights map (_ * oldWeightSum / newWeightSum)

        val nHit: Int = isCorrect count (_ == true)
        val nMiss: Int = isCorrect count (_ == false)
        val wHit: Double = (isCorrect zip currentSampleWeight).filter(_._1).map(_._2).sum
        val wMiss: Double = (isCorrect zip currentSampleWeight).filter(!_._1).map(_._2).sum
        val wNewHit: Double = (isCorrect zip newNormSampleWeight).filter(_._1).map(_._2).sum
        val wNewMiss: Double = (isCorrect zip newNormSampleWeight).filter(!_._1).map(_._2).sum
        println(" - hits: %d (weighted: %.2f)".format(nHit, wHit))
        println(" - misses: %d (weighted: %.2f)".format(nMiss, wMiss))

        val missFactor = wNewMiss / wMiss
        val hitFactor = wNewHit / wHit
        println(" - weight update of hits: %.2f".format(hitFactor))
        println(" - weight update of misses: %.2f".format(missFactor))

        println(" - confusion matrix")
        Evaluation.matrix(y_pred, y, percentage = false)

        // for now, take each learners f1 score as its weight
        trees.head.decisionTree.weight = Evaluation.f1(y_pred, y)
        println(" - setting learner weight: %.3f".format(trees.head.decisionTree.weight))

        require(Maths.round(newNormSampleWeight.sum, 6) == Maths.round(currentSampleWeight.sum, 6), "weight conservation violated")
        boost(trees.tail, newNormSampleWeight, step + 1)
      }
    }

    val startSampleWeight =
      if (sampleWeight.isEmpty) List.fill(y.length)(1.0)
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
