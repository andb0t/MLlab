package datastructures

import scala.collection.mutable.ListBuffer
import scala.util.control.Breaks._


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
  val nodes: Int = Math.pow(2, depth).toInt - 1
  for (i <- 0 until nodes){
    tree += new DecisionNode(i)
  }

  def updateNode(nodeIndex: Int, featureIndex: Int, threshold: Double, greater: Boolean, purity: Double): Unit = {
    if (tree(nodeIndex).purity < purity){
      // println("Improving purity of node " + nodeIndex +
      //   " with feature " + featureIndex +
      //   (if (greater) " > " else " < ") + "%+.3f".format(threshold) +
      //   ": " + "%.3e".format(tree(nodeIndex).purity) +
      //   " -> " + "%.3e".format(purity))
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

  def nFilledNodes(): Int = {
    var nFilled: Int = 0
    for (node <- tree) {
      if (node.filled) {
        nFilled += 1
      }
    }
    nFilled
  }

  override def toString(): String = {

    def printNodes(): String =
      (for { node <- tree if (node.filled) } yield
        "Node " + node.nodeIndex +
        ", decides on feature " + node.featureIndex +
        (if (node.greater) "> " else "< ") + "%+.3f".format(node.threshold) +
        ", parent " + node.parent +
        ", purity %.3e".format(node.purity) +
        " left child " + node.left + " right child " + node.right + "\n"
      ).mkString

    "------- Decision Tree -------\n" +
    "Tree complete with " + nFilledNodes() + " / " + nodes + " filled nodes\n" +
    printNodes +
    "------------------------------"
  }

  def classify(instance: List[Double]): Int = {
    var label = 0
    var currentNodeIndex = 0
    breakable {
      while (true) {
        val greater = tree(currentNodeIndex).greater
        val featureIndex = tree(currentNodeIndex).featureIndex
        val threshold = tree(currentNodeIndex).threshold
        val left = tree(currentNodeIndex).left
        val right = tree(currentNodeIndex).right

        if (!tree(currentNodeIndex).filled) break

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

  def atNode(nodeIndex: Int, X: List[List[Double]], y: List[Int]): (List[List[Double]], List[Int]) = {

    require(X.length == y.length, "both arguments must have the same length")

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
    // println("node " + nodeIndex + " has ancestors " + ancestors)

    // apply the corresponding cuts successively
    var newX = new ListBuffer[List[Double]]()
    var newy = new ListBuffer[Int]()
    X.copyToBuffer(newX)
    y.copyToBuffer(newy)
    for (ancestor <- ancestors) {
      // println("temporary length X " + newX.length + " y " + newy.length)
      val takeRightArm = ancestor._2
      val iFeature = tree(ancestor._1).featureIndex
      val threshold = tree(ancestor._1).threshold
      // println("ancestor " + ancestor._1 +
      //   " goes " + (if (takeRightArm) "right" else "left") +
      //   " with feature " + iFeature)
      assert (newX.length == newy.length)
      var tmpX = new ListBuffer[List[Double]]()
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

    // println("Node " + nodeIndex + " has " + newX.length + " entries")

    Tuple2(newX.toList, newy.toList)
  }


}
