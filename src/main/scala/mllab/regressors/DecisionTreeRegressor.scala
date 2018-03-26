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
   * @param threshold The cut-off threshold to be applied
   * @return The purity of this split
   */
  def getPurity(featureX: List[Double], threshold: Double): Double = {
    val belowX: List[Double] = featureX.filter(_ < threshold)
    val aboveX: List[Double] = featureX.filter(_ > threshold)
    val belowXMean: Double = belowX.sum / belowX.length
    val aboveXMean: Double = aboveX.sum / aboveX.length
    val cost: Double = Math.sqrt(belowX.map(x => Math.pow(x - belowXMean, 2)).sum + aboveX.map(x => Math.pow(x - aboveXMean, 2)).sum)
    val purity: Double = -cost
    // println("threshold " + threshold + " purity " + purity)
    purity
  }

  /** Sets optimal feature and cut-off value for this node
   * @param X Feature vectors of instances at this node
   * @param y List of labels of instances at this node
   * @param decTree Decision tree to update
   * @param nodeIndex Index of the node to tune
   */
  def setOptimalCut(X: List[List[Double]], y: List[Double], decTree: DecisionTree, nodeIndex: Int): Unit = {
    // println("Tuning node " + nodeIndex)
    val nSteps: Int = 10
    val nZooms: Int = 3
    val mean: Double = 1.0 * y.sum / y.length

    def zoom(count: Int, min: Double, stepSize: Double, featureX: List[Double], iFeature: Int): Unit =
      if (count < nZooms) {

        def scanSteps(count: Int, purestSplit: Double, maxPurity: Double): Unit =
          if (count < nSteps) {
            val currThresh: Double = min + count * stepSize
            val currPurity = getPurity(featureX, currThresh)
            decTree.updateNode(nodeIndex, iFeature, currThresh, true, mean, currPurity)
            if (maxPurity < currPurity) scanSteps(count+1, currThresh, currPurity)
            else scanSteps(count+1, purestSplit, maxPurity)
          }

        scanSteps(0, 0, Double.MinValue)
        val newMin: Double = -stepSize
        val newStepSize: Double = 2.0 * stepSize / (nSteps - 1)
        zoom(count+1, newMin, newStepSize, featureX, iFeature)
      }

    if (!X.isEmpty) {
      val nFeatures: Int = X.head.length
      for (iFeature <- 0 until nFeatures){
        val featureX: List[Double] = X.map(_.apply(iFeature))
        val featureMax: Double = featureX.max
        val featureMin: Double = featureX.min
        val range: Double = featureMax - featureMin
        val max: Double = featureMax + 0.01 * range
        val min: Double = featureMin - 0.01 * range
        val stepSize: Double = (max - min) / (nSteps - 1)
        zoom(0, min, stepSize, featureX, iFeature)
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
