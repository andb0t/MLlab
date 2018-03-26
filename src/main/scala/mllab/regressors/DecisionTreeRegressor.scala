package regressors

import datastructures._


/** Decision tree regressor
 *
 * @param depth Depth of the tree
 */
class DecisionTreeRegressor(depth: Int = 6) extends Regressor {

  val name: String = "DecisionTreeRegressor"

  var decisionTree = new DecisionTree(depth)

  /** Calculates the purity for a cut at threshold at this node
   * @param featureX List of this feature for all instances at this node
   * @param yThisNode List of the corresponding labels
   * @param threshold The cut-off threshold to be applied
   * @return The purity of this split and a boolean flat indicating if signal region is greater than the threshold
   */
  def getPurity(featureX: List[Double], yThisNode: List[Double], threshold: Double): (Double, Boolean) = {

    val belowX: List[Double] = featureX.filter(_ < threshold)
    val aboveX: List[Double] = featureX.filter(_ > threshold)
    val belowXMean: Double = belowX.sum / belowX.length
    val aboveXMean: Double = aboveX.sum / aboveX.length
    val cost: Double = Math.sqrt(belowX.map(x => Math.pow(x - belowXMean, 2)).sum + aboveX.map(x => Math.pow(x - aboveXMean, 2)).sum)
    val purity: Double = -cost
    // println("threshold " + threshold + " purity " + purity)
    Tuple2(purity, true)
  }

  /** Sets optimal feature and cut-off value for this node
   * @param X Feature vectors of instances at this node
   * @param y List of labels of instances at this node
   * @param decTree Decision tree to update
   * @param nodeIndex Index of the node to tune
   */
  def setOptimalCut(X: List[List[Double]], y: List[Double], decTree: DecisionTree, nodeIndex: Int): Unit = {
    println("Tuning node " + nodeIndex)
    val nSteps = 10
    val nZooms = 3
    val mean: Double = 1.0 * y.sum / y.length

    if (!X.isEmpty) {
      val nFeatures: Int = X.head.length
      for (iFeature <- 0 until nFeatures){
        val featureX = X.map(_.apply(iFeature))
        val featureMax: Double = featureX.max
        val featureMin: Double = featureX.min
        val range = featureMax - featureMin

        var max = featureMax + 0.01 * range
        var min = featureMin - 0.01 * range
        var stepSize = (max - min) / (nSteps - 1)
        var purestStep: Double = 0
        var maxPurity: Double = Double.MinValue

        for (i <- 0 until nZooms) {
          for (i <- 0 until nSteps) {
            val currThresh: Double = min + i * stepSize
            val (currPurity, currGreater) = getPurity(featureX, y, currThresh)
            decTree.updateNode(nodeIndex, iFeature, currThresh, currGreater, mean, currPurity)
            if (maxPurity < currPurity) {
              maxPurity = currPurity
              purestStep = currThresh
            }
          }
          max = purestStep + stepSize
          min = purestStep - stepSize
          stepSize = (max - min) / (nSteps - 1)
          purestStep = 0
          maxPurity = Double.MinValue
        }

      }
    }
  }

  def train(X: List[List[Double]], y: List[Double]): Unit = {
    require(X.length == y.length, "number of training instances and labels is not equal")
    for (nodeIndex <- 0 until decisionTree.nNodes){
      val (thisNodeX, yThisNode) = decisionTree.atNode(nodeIndex, X, y)
      setOptimalCut(thisNodeX, yThisNode, decisionTree, nodeIndex)
    }
    println(decisionTree)
  }

  def predict(X: List[List[Double]]): List[Double] =
    for (x <- X) yield decisionTree.predict(x)

}
