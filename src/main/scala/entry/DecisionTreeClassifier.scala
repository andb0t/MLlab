package mllab

import scala.collection.mutable.ListBuffer
import scala.util.control.Breaks._


class DecisionTreeClassifier(depth: Int = 3, purityMeasure: String="gini") {

  var decisionTree = new DecisionTree(depth)

  def train(X: List[List[Float]], y: List[Int]): Unit = {
    assert (X.length == y.length)

    def getPurity(xThisFeature: List[Float], yThisNode: List[Int], threshold: Double): (Double, Boolean) = {
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
        var cost: Double = Double.MaxValue
        if (mLeft * mRight != 0) {
          val GLeft: Double = 1.0 - Math.pow(1.0 * leftSig/mLeft, 2) - Math.pow(1.0 * leftBkg/mLeft, 2)
          val GRight: Double = 1.0 - Math.pow(1.0 * rightSig/mRight, 2) - Math.pow(1.0 * rightBkg/mRight, 2)
          cost = GLeft * mLeft / m + GRight * mRight / m
        }
        purity = -cost
      }
      // println("threshold " + threshold + " purity " + purity)
      Tuple2(purity, greater)
    }

    def setOptimalCut(nodeIndex: Int): Unit = {

      val nSteps = 10
      val nZooms = 3

      val nFeatures: Int = X.head.length
      var (xThisNode, yThisNode) = decisionTree.atNode(nodeIndex, X, y)
      for (iFeature <- 0 until nFeatures){
        var xThisFeature = xThisNode.map(_.apply(iFeature))
        // println(x.take(10))
        // println(y.take(10))

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
            var (currPurity, currGreater) = getPurity(xThisFeature, yThisNode, currThresh)
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

    for (nodeIndex <- 0 until decisionTree.nodes){
      setOptimalCut(nodeIndex)
    }
    decisionTree.print()
  }

  def predict(X: List[List[Float]]): List[Int] = {
    var result = new ListBuffer[Int]()
    for (x <- X) {
      result += decisionTree.classify(x)
    }
    result.toList
  }
}

class DecisionNode(nIndex: Int){
  val nodeIndex: Int = nIndex
  var right: Int = (nodeIndex + 1) * 2
  var left: Int = (nodeIndex + 1) * 2 - 1
  val parent: Int = if (nodeIndex == 0) -1 else (nodeIndex - 1) / 2
  val isRightChild: Boolean = (nodeIndex % 2 == 0)

  var featureIndex: Int = -1
  var threshold: Double = 0
  var greater: Boolean = true
  var filled: Boolean = false
  var purity: Double = Double.MinValue
}

class DecisionTree(depth: Int){

  var tree = new ListBuffer[DecisionNode]()
  var nodes: Int = Math.pow(2, depth).toInt - 1
  for (i <- 0 until nodes){
    tree += new DecisionNode(i)
  }

  def updateNode(nodeIndex: Int, featureIndex: Int, threshold: Double, greater: Boolean, purity: Double): Unit = {
    if (tree(nodeIndex).purity >= purity){
    }else{
      println("Improving purity of node " + nodeIndex +
        " with feature " + featureIndex +
        (if (greater) " > " else " < ") + "%+.3f".format(threshold) +
        ": " + "%.3e".format(tree(nodeIndex).purity) +
        " -> " + "%.3e".format(purity))
        addNode(nodeIndex, featureIndex, threshold, greater, purity)
    }
  }

  def addNode(nodeIndex: Int, featureIndex: Int, threshold: Double, greater: Boolean, purity: Double=Double.MinValue): Unit = {
    if (nodeIndex > tree.length - 1) {
      println("Warning: tree not deep enough! (" + nodeIndex + " > " + (tree.length - 1) + ") Ignore node.")
    }else{
      tree(nodeIndex).featureIndex = featureIndex
      tree(nodeIndex).threshold = threshold
      tree(nodeIndex).greater = greater
      tree(nodeIndex).filled = true
      if (tree(nodeIndex).right >= nodes) tree(nodeIndex).right = -1
      if (tree(nodeIndex).left >= nodes) tree(nodeIndex).left = -1
      tree(nodeIndex).purity = purity
    }
  }

  def isComplete(): Boolean = {
    var complete = true
    breakable{
      for (node <- tree) {
        if (!node.filled) {
          complete = false
          break
        }
      }
    }
    complete
  }

  def print(): Unit = {
    println("------- Decision Tree -------")
    println("Tree complete? " + isComplete() + " with " + nodes + " nodes")
    for (node <- tree) {
      println("Node " + node.nodeIndex +
        ", decides on feature " + node.featureIndex +
        (if (node.greater) "> " else "< ") + "%+.3f".format(node.threshold) +
        ", parent " + node.parent +
        ", purity %.3e".format(node.purity) +
        " left child " + node.left + " right child " + node.right)
    }
    println("------------------------------")
  }

  def classify(instance: List[Float]): Int = {
    assert (isComplete)
    var label = 0
    var currentNodeIndex = 0
    breakable {
      while (true) {
        val greater = tree(currentNodeIndex).greater
        val featureIndex = tree(currentNodeIndex).featureIndex
        val threshold = tree(currentNodeIndex).threshold
        val left = tree(currentNodeIndex).left
        val right = tree(currentNodeIndex).right
        if (greater) {
          if (instance(featureIndex) > threshold) {
            label = 1
            currentNodeIndex = right
          }
          else{
            label = 0
            currentNodeIndex = left
          }
        }
        else{
          if (instance(featureIndex) < threshold) {
            label = 1
            currentNodeIndex = left
          }
          else{
            label = 0
            currentNodeIndex = right
          }
        }
        if (currentNodeIndex == -1) break
      }
    }
    label
  }

  def atNode(nodeIndex: Int, X: List[List[Float]], y: List[Int]): (List[List[Float]], List[Int]) = {

    assert (X.length == y.length)

    // determine ancestors of this node
    var ancestors = new ListBuffer[(Int, Boolean)]()
    var currentNode = nodeIndex
    breakable{
      while (true) {
        val parent = tree(currentNode).parent
        val isRightChild = tree(currentNode).isRightChild
        if (parent == -1) break
        ancestors += Tuple2(parent, isRightChild)
        currentNode = parent
      }
    }
    ancestors = ancestors.reverse
    println("node " + nodeIndex + " has ancestors " + ancestors)

    // apply the corresponding cuts successively
    var newX = new ListBuffer[List[Float]]()
    var newy = new ListBuffer[Int]()
    X.copyToBuffer(newX)
    y.copyToBuffer(newy)
    for (ancestor <- ancestors) {
      println("temporary length X " + newX.length + " y " + newy.length)
      val takeRightArm = ancestor._2
      val iFeature = tree(ancestor._1).featureIndex
      val threshold = tree(ancestor._1).threshold
      println("ancestor " + ancestor._1 +
        " goes " + (if (takeRightArm) "right" else "left") +
        " with feature " + iFeature)
      assert (newX.length == newy.length)
      var tmpX = new ListBuffer[List[Float]]()
      var tmpy = new ListBuffer[Int]()
      for (i <- 0 until newX.length){
        val feature = newX(i).apply(iFeature)
        if ((takeRightArm && feature > threshold) ||
            (!takeRightArm && feature <= threshold)) {
          tmpX += newX(i)
          tmpy += newy(i)
        }
      }
      newX.clear()
      newy.clear()
      tmpX.copyToBuffer(newX)
      tmpy.copyToBuffer(newy)
    }
    println("Final length X " + newX.length + " y " + newy.length)

    Tuple2(newX.toList, newy.toList)
  }


}
