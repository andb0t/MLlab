package mllab

import scala.collection.mutable.ListBuffer
import scala.util.control.Breaks._


class DecisionTreeClassifier(depth: Int = 3) {

  var decisionTree = new DecisionTree(depth)

  def train(X: List[List[Float]], y: List[Int]): Unit = {
    assert (X.length == y.length)
    decisionTree.addNode(0, 0, 0, true)
    decisionTree.addNode(1, 1, 0, true)
    decisionTree.addNode(2, 1, 0, false)
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
}

class DecisionTree(depth: Int){

  var tree = new ListBuffer[DecisionNode]()
  var nodes: Int = Math.pow(2, depth).toInt - 1
  for (i <- 0 until nodes){
    tree += new DecisionNode()
  }

  def addNode(nodeIndex: Int, featureIndex: Int, threshold: Double, greater: Boolean): Unit = {
    if (nodeIndex > tree.length - 1) {
      println("Warning: tree not deep enough! (" + nodeIndex + " > " + (tree.length - 1) + ") Ignore node.")
      return
    }
    if (tree(nodeIndex).filled) println("Warning: overwriting decision tree node " + nodeIndex)
    tree(nodeIndex).nodeIndex = nodeIndex
    tree(nodeIndex).featureIndex = featureIndex
    tree(nodeIndex).threshold = threshold
    tree(nodeIndex).greater = greater
    tree(nodeIndex).filled = true
    tree(nodeIndex).right = if ((nodeIndex + 1) * 2 < tree.length) (nodeIndex + 1) * 2 else -1
    tree(nodeIndex).left = if ((nodeIndex + 1) * 2 - 1 < tree.length) (nodeIndex + 1) * 2 - 1 else -1
    tree(nodeIndex).parent = if (nodeIndex == 0) -1 else (nodeIndex - 1) / 2
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
        ", decides on " + node.featureIndex +
        " signal " + (if (node.greater) "> " else "< ") + node.threshold +
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
