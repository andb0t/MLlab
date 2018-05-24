package classifiers

import play.api.libs.json.JsValue

import json._


/** Companion object providing default parameters */
object BoostedDecisionTreeClassifier {
  val depth: Int = 3
  val criterion: String="gini"
  val minSamplesSplit: Int = 2
  val n_estimators: Int = 3
}

/** Decision tree classifier
 * @param depth Depth of the tree
 * @param criterion Function to measure the quality of a split
 * @param minSamplesSplit Minimum number of samples required to split an internal node
 */
class BoostedDecisionTreeClassifier(
  depth: Int = BoostedDecisionTreeClassifier.depth,
  criterion: String = BoostedDecisionTreeClassifier.criterion,
  minSamplesSplit: Int =  BoostedDecisionTreeClassifier.minSamplesSplit,
  n_estimators: Int =  BoostedDecisionTreeClassifier.n_estimators
) extends Classifier {
  def this(json: JsValue) = {
    this(
      depth = JsonMagic.toInt(json, "depth", BoostedDecisionTreeClassifier.depth),
      criterion = JsonMagic.toString(json, "criterion", BoostedDecisionTreeClassifier.criterion),
      minSamplesSplit = JsonMagic.toInt(json, "minSamplesSplit", BoostedDecisionTreeClassifier.minSamplesSplit),
      n_estimators = JsonMagic.toInt(json, "n_estimators", BoostedDecisionTreeClassifier.n_estimators)
      )
  }

  val name: String = "BoostedDecisionTreeClassifier"

  println(s"Initializing $n_estimators decision trees ...")
  val trees = List.fill(n_estimators)(
    new DecisionTreeClassifier(
      depth = depth,
      criterion = criterion,
      minSamplesSplit = minSamplesSplit
    )
  )

  def train(X: List[List[Double]], y: List[Int], sampleWeight: List[Double] = Nil): Unit = {

    def boost(trees: List[DecisionTreeClassifier], currentSampleWeight: List[Double], step: Int = 0): List[Double] = {
      if (trees.isEmpty) currentSampleWeight
      else {
        println(s"Train tree $step")
        trees.head.train(X, y, currentSampleWeight)
        val updatedSampleWeights = Nil
        boost(trees.tail, updatedSampleWeights, step + 1)
      }
    }

    val startSampleWeight =
      if (sampleWeight.isEmpty) List.fill(y.length)(1.0)
      else sampleWeight

    boost(trees, startSampleWeight)

  }

  def predict(X: List[List[Double]]): List[Int] =
    trees.last.predict(X)

}
