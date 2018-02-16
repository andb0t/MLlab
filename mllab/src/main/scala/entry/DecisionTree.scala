package mllab

import scala.collection.mutable.ListBuffer


class DecisionTreeClassifier(depth: Int = 3) {

  var decisionTree = new DecisionTree(depth)

  def train(X: List[List[Float]], y: List[Int]): Unit = {
    assert (X.length == y.length)
    decisionTree.addNode(0, 0, 0, true)
    decisionTree.addNode(1, 1, 0, true)
    decisionTree.addNode(2, 1, 0, true)
    decisionTree.print()
  }

  def predict(X: List[List[Float]]): List[Int] = {

    // var result = new ListBuffer[Int]()
    //
    // for (decision <- decisionSequence) {
    //   var (index, threshold, right) = decision
    //   println("Current decision: index " + index +
    //     " threshold " + threshold +
    //     " signal right? " + right)
    //
    //   for (x <- X) {
    //
    //   }
    //
    // }

    val result = for (x <- X) yield Math.random.round
    result.toList.map(_.toInt)
  }
}

class DecisionNode(){
  var nodeIndex: Int = -1
  var featureIndex: Int = -1
  var threshold: Double = 0
  var signalRight: Boolean = true
  var filled: Boolean = false
  var right: Int = -1
  var left: Int = -1
  var parent: Int = -1
}

class DecisionTree(depth: Int){

  var tree = new ListBuffer[DecisionNode]()
  for (i <- 0 until Math.pow(2, depth).toInt - 1){
    tree += new DecisionNode()
  }

  def addNode(nodeIndex: Int, featureIndex: Int, threshold: Double, signalRight: Boolean): Unit = {
    if (nodeIndex > tree.length - 1) {
      println("Warning: tree not deep enough! (" + nodeIndex + " > " + (tree.length - 1) + ") Ignore node.")
      return
    }
    if (tree(nodeIndex).filled) println("Warning: overwriting decision tree node " + nodeIndex)
    tree(nodeIndex).nodeIndex = nodeIndex
    tree(nodeIndex).featureIndex = featureIndex
    tree(nodeIndex).threshold = threshold
    tree(nodeIndex).signalRight = signalRight
    tree(nodeIndex).filled = true
    tree(nodeIndex).right = if ((nodeIndex + 1) * 2 < tree.length) (nodeIndex + 1) * 2 else -1
    tree(nodeIndex).left = if ((nodeIndex + 1) * 2 - 1 < tree.length) (nodeIndex + 1) * 2 - 1 else -1
    tree(nodeIndex).parent = if (nodeIndex == 0) -1 else (nodeIndex - 1) / 2

  }

  def isComplete(): Boolean = {
    var complete = true
    for (node <- tree) {
      if (!node.filled) complete = false
    }
    complete
  }

  def print(): Unit = {
    println("------- Decision Tree -------")
    println("Tree complete? " + isComplete())
    for (node <- tree) {
      println("Node " + node.nodeIndex + ", decides on " + node.featureIndex +
          ", parent " + node.parent +" left child " + node.left + " right child " + node.right)
    }
    println("------------------------------")
  }

}
