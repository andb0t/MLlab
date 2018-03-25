package classifiers

import datastructures._

import scala.collection.mutable.ListBuffer


/** Decision tree classifier
 * @param depth Depth of the tree
 * @param criterion Function to measure the quality of a split
 */
class DecisionTreeClassifier(depth: Int = 3, criterion: String="gini") extends Classifier {

  val name: String = "DecisionTreeClassifier"

  var decisionTree = new DecisionTree(depth)

  /** Calculates the purity for a cut at threshold at this node
   * @param featureX List of this feature for all instances at this node
   * @param yThisNode List of the corresponding labels
   * @param threshold The cut-off threshold to be applied
   * @param crit Function to measure the quality of a split
   * @return The purity of this split and a boolean flat indicating if signal region is greater than the threshold
   */
  def getPurity(featureX: List[Double], yThisNode: List[Int], threshold: Double, crit: String): (Double, Boolean) = {
    val rightIdx = featureX.zip(yThisNode).filter(tup => tup._1 > threshold).map(tup => tup._2)
    val leftIdx = featureX.zip(yThisNode).filter(tup => tup._1 <= threshold).map(tup => tup._2)
    val rightSig: Int = rightIdx.count(_ == 1)
    val rightBkg: Int = rightIdx.count(_ == 0)
    val leftSig: Int = leftIdx.count(_ == 1)
    val leftBkg: Int = leftIdx.count(_ == 0)
    val greater: Boolean = (rightSig > leftSig)
    val purity =
      if (crit == "maxcorrect"){
        1.0 * (rightSig + leftBkg) / featureX.length
      }
      else if (crit == "gini"){
        // CART cost function
        val m: Int = featureX.length
        val mLeft: Int = leftIdx.length
        val mRight: Int = rightIdx.length
        val GLeft: Double =
          if (mLeft != 0) 1.0 - Math.pow(1.0 * leftSig/mLeft, 2) - Math.pow(1.0 * leftBkg/mLeft, 2)
          else 0
        val GRight: Double =
          if (mRight != 0) 1.0 - Math.pow(1.0 * rightSig/mRight, 2) - Math.pow(1.0 * rightBkg/mRight, 2)
          else 0
        println(GRight)
        println(GLeft)
        val cost = GLeft * mLeft / m + GRight * mRight / m
        -cost
      }
      else Double.MinValue
    // println("threshold " + threshold + " purity " + purity)
    Tuple2(purity, greater)
  }

  /** Sets optimal feature and cut-off value for this node
   * @param X Feature vectors of instances at this node
   * @param y List of labels of instances at this node
   * @param decTree Decision tree to update
   * @param nodeIndex Index of the node to tune
   */
  def setOptimalCut(X: List[List[Double]], y: List[Int], decTree: DecisionTree, nodeIndex: Int): Unit = {
    println("Tuning node " + nodeIndex)
    val nSteps = 10
    val nZooms = 3

    val nFeatures: Int = X.head.length
    if (!X.isEmpty) {
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
            val (currPurity, currGreater) = getPurity(featureX, y, currThresh, criterion)
            decTree.updateNode(nodeIndex, iFeature, currThresh, currGreater, currPurity)
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

  def train(X: List[List[Double]], y: List[Int]): Unit = {
    require(X.length == y.length, "number of training instances and labels is not equal")
    for (nodeIndex <- 0 until decisionTree.nNodes){
      val (thisNodeX, yThisNode) = decisionTree.atNode(nodeIndex, X, y)
      setOptimalCut(thisNodeX, yThisNode, decisionTree, nodeIndex)
    }
    println(decisionTree)
  }

  def predict(X: List[List[Double]]): List[Int] =
    for (x <- X) yield decisionTree.classify(x)

}
