package mllab

import scala.collection.mutable.ListBuffer
import scala.util.control.Breaks._


class DecisionTreeClassifier(depth: Int = 3) {

  var decisionTree = new DecisionTree(depth)

  def train(X: List[List[Float]], y: List[Int]): Unit = {
    assert (X.length == y.length)

    val nFeatures: Int = X(0).length
    val nSteps = 10

    def getPurity(x: List[Float], y: List[Int], threshold: Double): (Double, Boolean) = {
      var gtIdx = x.zip(y).filter(tup => tup._1 > threshold).map(tup => tup._2)
      var ltIdx = x.zip(y).filter(tup => tup._1 <= threshold).map(tup => tup._2)
      var TP = gtIdx.filter(_ == 1).length
      var FP = gtIdx.filter(_ == 0).length
      var TN = ltIdx.filter(_ == 0).length
      var FN = ltIdx.filter(_ == 1).length
      val greater = (TP > FN)
      val purity: Double = 1.0 * (TP + TN) / x.length
      // println("threshold " + threshold + " purity " + purity)
      return Tuple2(purity, greater)
    }

    def setOptimalCut(nodeIndex: Int): Unit = {
      for (iFeature <- 0 until nFeatures){
        var x = X.map(_.apply(iFeature))
        // println(x.take(10))
        // println(y.take(10))

        var max = x.max
        var min  = x.min
        var stepSize = (max - min) / (nSteps - 1)
        for (i <- 0 until nSteps) {
          val currThresh: Double = min + i * stepSize
          // var currPurity: Double = Double.MinValue
          // var currGreater: Boolean = true
          var (currPurity, currGreater) = getPurity(x, y, currThresh)
          decisionTree.updateNode(nodeIndex, iFeature, currThresh, currGreater, currPurity)
        }
      }
    }

    for (nodeIndex <- 0 until decisionTree.nodes){
      setOptimalCut(nodeIndex)
    }

    // decisionTree.addNode(0, 0, 0, true)
    // decisionTree.addNode(1, 1, 0, true)
    // decisionTree.addNode(2, 1, 0, false)
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

class DecisionNode(){
  var nodeIndex: Int = -1
  var featureIndex: Int = -1
  var threshold: Double = 0
  var greater: Boolean = true
  var filled: Boolean = false
  var right: Int = -1
  var left: Int = -1
  var parent: Int = -1
  var purity: Double = Double.MinValue
}

class DecisionTree(depth: Int){

  var tree = new ListBuffer[DecisionNode]()
  var nodes: Int = Math.pow(2, depth).toInt - 1
  for (i <- 0 until nodes){
    tree += new DecisionNode()
  }

  def updateNode(nodeIndex: Int, featureIndex: Int, threshold: Double, greater: Boolean, purity: Double): Unit = {
    if (tree(nodeIndex).purity >= purity){
      return
    }
    println("Improving purity of node " + nodeIndex +
      " with feature " + featureIndex +
      (if (greater) " > " else " < ") + threshold +
      ": " + tree(nodeIndex).purity +
      " -> " + purity)
    addNode(nodeIndex, featureIndex, threshold, greater, purity)
  }

  def addNode(nodeIndex: Int, featureIndex: Int, threshold: Double, greater: Boolean, purity: Double=Double.MinValue): Unit = {
    if (nodeIndex > tree.length - 1) {
      println("Warning: tree not deep enough! (" + nodeIndex + " > " + (tree.length - 1) + ") Ignore node.")
      return
    }
    tree(nodeIndex).nodeIndex = nodeIndex
    tree(nodeIndex).featureIndex = featureIndex
    tree(nodeIndex).threshold = threshold
    tree(nodeIndex).greater = greater
    tree(nodeIndex).filled = true
    tree(nodeIndex).right = if ((nodeIndex + 1) * 2 < tree.length) (nodeIndex + 1) * 2 else -1
    tree(nodeIndex).left = if ((nodeIndex + 1) * 2 - 1 < tree.length) (nodeIndex + 1) * 2 - 1 else -1
    tree(nodeIndex).parent = if (nodeIndex == 0) -1 else (nodeIndex - 1) / 2
    tree(nodeIndex).purity = purity
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
        (if (node.greater) "> " else "< ") + node.threshold +
        ", parent " + node.parent +" left child " + node.left + " right child " + node.right)
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
    return label
  }

}
