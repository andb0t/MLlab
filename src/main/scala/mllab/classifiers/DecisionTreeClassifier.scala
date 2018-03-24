package classifiers

import datastructures._

import scala.collection.mutable.ListBuffer


/** Decision tree classifier
 * @param depth Depth of the tree
 * @param purityMeasure Purity measure to decide on optimal cut features and thresholds
 */
class DecisionTreeClassifier(depth: Int = 3, purityMeasure: String="gini") extends Classifier {

  val name: String = "DecisionTreeClassifier"

  var decisionTree = new DecisionTree(depth)

  def train(X: List[List[Double]], y: List[Int]): Unit = {
    require(X.length == y.length, "number of training instances and labels is not equal")

    def getPurity(xThisFeature: List[Double], yThisNode: List[Int], threshold: Double): (Double, Boolean) = {
      val rightIdx = xThisFeature.zip(yThisNode).filter(tup => tup._1 > threshold).map(tup => tup._2)
      val leftIdx = xThisFeature.zip(yThisNode).filter(tup => tup._1 <= threshold).map(tup => tup._2)
      val rightSig: Int = rightIdx.count(_ == 1)
      val rightBkg: Int = rightIdx.count(_ == 0)
      val leftBkg: Int = leftIdx.count(_ == 0)
      val leftSig: Int = leftIdx.count(_ == 1)
      val m: Int = xThisFeature.length
      val greater: Boolean = (rightSig > leftSig)
      var purity: Double = Double.MinValue
      if (purityMeasure == "maxcorrect"){
        purity = 1.0 * (rightSig + leftBkg) / m
      }
      else if (purityMeasure == "gini"){
        // CART cost function
        val mLeft: Int = leftIdx.length
        val mRight: Int = rightIdx.length
        var GLeft: Double = 0
        var GRight: Double = 0
        if (mLeft != 0) {
          GLeft = 1.0 - Math.pow(1.0 * leftSig/mLeft, 2) - Math.pow(1.0 * leftBkg/mLeft, 2)
        }
        if (mRight != 0) {
          GRight = 1.0 - Math.pow(1.0 * rightSig/mRight, 2) - Math.pow(1.0 * rightBkg/mRight, 2)
        }
        val cost = GLeft * mLeft / m + GRight * mRight / m
        purity = -cost
      }
      // println("threshold " + threshold + " purity " + purity)
      Tuple2(purity, greater)
    }

    def setOptimalCut(nodeIndex: Int): Unit = {

      println("Tuning node " + nodeIndex)

      val nSteps = 10
      val nZooms = 3

      val nFeatures: Int = X.head.length
      val (xThisNode, yThisNode) = decisionTree.atNode(nodeIndex, X, y)
      if (!xThisNode.isEmpty) {
        for (iFeature <- 0 until nFeatures){
          var xThisFeature = xThisNode.map(_.apply(iFeature))

          var max: Double = xThisFeature.max
          var min: Double = xThisFeature.min
          max = max + 0.01 * (max - min)
          min = min - 0.01 * (max - min)
          var stepSize = (max - min) / (nSteps - 1)
          var purestStep: Double = 0
          var maxPurity: Double = Double.MinValue

          for (i <- 0 until nZooms) {
            for (i <- 0 until nSteps) {
              val currThresh: Double = min + i * stepSize
              val (currPurity, currGreater) = getPurity(xThisFeature, yThisNode, currThresh)
              decisionTree.updateNode(nodeIndex, iFeature, currThresh, currGreater, currPurity)
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

    for (nodeIndex <- 0 until decisionTree.nNodes){
      setOptimalCut(nodeIndex)
    }
    println(decisionTree)
  }

  def predict(X: List[List[Double]]): List[Int] =
    for (x <- X) yield decisionTree.classify(x)

}
