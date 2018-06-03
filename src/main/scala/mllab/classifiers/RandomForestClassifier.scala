package classifiers

import play.api.libs.json.JsValue

import evaluation._
import json._
import utils._



class predictor(clf: Classifier) {
  val pred = clf
  var weight = 1.0
  var featureIndices: List[Int] = Nil
}

/** Companion object providing default parameters */
object RandomForestClassifier {
  val depth: Int = 3
  val criterion: String="gini"
  val minSamplesSplit: Int = 2
  val nEstimators: Int = 3
  val verbose: Int = 1
}

/** Decision tree classifier
 * @param depth Depth of the tree
 * @param criterion Function to measure the quality of a split
 * @param minSamplesSplit Minimum number of samples required to split an internal node
 * @param nEstimators Number of boosting steps
 * @param verbose Verbosity of output
 */
class RandomForestClassifier(
  depth: Int = RandomForestClassifier.depth,
  criterion: String = RandomForestClassifier.criterion,
  minSamplesSplit: Int =  RandomForestClassifier.minSamplesSplit,
  nEstimators: Int =  RandomForestClassifier.nEstimators,
  verbose: Int =  RandomForestClassifier.verbose
) extends Classifier {
  def this(json: JsValue) = {
    this(
      depth = JsonMagic.toInt(json, "depth", RandomForestClassifier.depth),
      criterion = JsonMagic.toString(json, "criterion", RandomForestClassifier.criterion),
      minSamplesSplit = JsonMagic.toInt(json, "minSamplesSplit", RandomForestClassifier.minSamplesSplit),
      nEstimators = JsonMagic.toInt(json, "nEstimators", RandomForestClassifier.nEstimators),
      verbose = JsonMagic.toInt(json, "verbose", RandomForestClassifier.verbose)
      )
  }

  val name: String = "RandomForestClassifier"

  println(s"Initializing $nEstimators decision trees ...")
  val trees = List.fill(nEstimators)(
    new predictor(
      new DecisionTreeClassifier(
        depth = depth,
        criterion = criterion,
        minSamplesSplit = minSamplesSplit,
        verbose = math.max(0, verbose - 1)
      )
    )
  )

  def train(X: List[List[Double]], y: List[Int], sampleWeight: List[Double] = Nil): Unit = {

    def trainRandom(X: List[List[Double]], y: List[Int], sampleWeight: List[Double], pred: predictor): Unit = {
      val nFeatures = X.head.length
      val featureIndices = scala.util.Random.shuffle((0 until nFeatures).toList)
      val maxFeatures = scala.util.Random.nextInt(2) + 1
      val thisFeatures = featureIndices.take(maxFeatures)
      val subX = X.map(Trafo.iloc(_, thisFeatures))
      println(s"Training classifier on $maxFeatures / $nFeatures features: " + thisFeatures)
      pred.pred.train(subX, y, sampleWeight)
      pred.featureIndices = thisFeatures
    }

    trees.map(trainRandom(X, y, sampleWeight, _))
  }

  def predict(X: List[List[Double]]): List[Int] = {
    val treeWeights = trees.map(_.weight)
    val relWeights = treeWeights.map(_ / treeWeights.sum)

    def maxVote(x: List[Double]): Int = {
      val grouped =
        (for ((tree, weight) <- (trees zip relWeights)) yield (tree.pred.predict(List(Trafo.iloc(x, tree.featureIndices))), weight))
        .groupBy(_._1)
        .mapValues(_.map(_._2).sum)
      grouped.maxBy(_._2)._1.head
    }

    X.map(maxVote(_))
  }

}
