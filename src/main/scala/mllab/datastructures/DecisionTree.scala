package datastructures

import scala.collection.mutable.ListBuffer
import scala.util.control.Breaks._


/**
  * A class representing a single node in a decision tree
  *
  * @constructor Create a new node
  * @param index Index of the node top-left to down-right
  */
class DecisionNode(index: Int){
  val nodeIndex: Int = index
  /** Index of right child */
  var right: Int = (nodeIndex + 1) * 2
  /** Index of left child */
  var left: Int = (nodeIndex + 1) * 2 - 1
  /** Index of parent */
  val parent: Int = if (nodeIndex == 0) -1 else (nodeIndex - 1) / 2
  /** Is this node a right child? */
  val isRightChild: Boolean = (nodeIndex % 2 == 0)

  /** Index of feature column this node decides on */
  var featureIndex: Int = -1
  /** Threshold this node applies */
  var threshold: Double = 0
  /** Is the signal greater or less than the threshold? */
  var greater: Boolean = true
  /** Has this node been touched since initialization? */
  var filled: Boolean = false
  /** Node purity */
  var purity: Double = Double.MinValue
}

/**
  * A class representing a decision tree
  *
  * @constructor Create a new decision tree
  * @param depth Depth of the tree
  */
class DecisionTree(depth: Int){

  /** Number of nodes in this tree */
  val nNodes: Int = Math.pow(2, depth).toInt - 1

  /**
  * Recursive function to initialize a list of nodes
  * @param nNodes number of nodes to initialize
  * @param tree start/intermediate tree object
  * @return List of nodes
  */
  def initTree(nNodes: Int, tree: List[DecisionNode]): List[DecisionNode] =
    if (tree.length < nNodes) initTree(nNodes, new DecisionNode(tree.length)::tree)
    else tree.reverse

  /** The object holding the nodes */
  val tree = initTree(nNodes, Nil)

  /**
   * Update an existing node with new decision instructions, in case its purity is improved
   * @param nodeIndex The index of the node to be customized
   * @param featureIndex The index of the feature the decision is based on
   * @param threshold The threshold of the proposed decision
   * @param greater Is the signal region greater or less than the threshold?
   * @param purity The purity of the proposed split
   */
  def updateNode(nodeIndex: Int, featureIndex: Int, threshold: Double, greater: Boolean, purity: Double): Unit = {
    if (tree(nodeIndex).purity < purity){
      // println("Improving purity of node " + nodeIndex +
      //   " with feature " + featureIndex +
      //   (if (greater) " > " else " < ") + "%+.3f".format(threshold) +
      //   ": " + "%.3e".format(tree(nodeIndex).purity) +
      //   " -> " + "%.3e".format(purity))
        setNode(nodeIndex, featureIndex, threshold, greater, purity)
    }
  }

  /**
   * Set node attributes
   * @param nodeIndex The index of the node to be customized
   * @param featureIndex The index of the feature the node decides on
   * @param threshold The threshold the node's decision will apply
   * @param greater Is the signal region greater or less than the threshold?
   * @param purity The purity of the split in this node
   */
  def setNode(nodeIndex: Int, featureIndex: Int, threshold: Double, greater: Boolean, purity: Double=Double.MinValue): Unit = {
    if (nodeIndex > tree.length - 1) {
      println("Warning: tree not deep enough! (" + nodeIndex + " > " + (tree.length - 1) + ") Ignore node.")
    }else{
      tree(nodeIndex).featureIndex = featureIndex
      tree(nodeIndex).threshold = threshold
      tree(nodeIndex).greater = greater
      tree(nodeIndex).filled = true
      if (tree(nodeIndex).right >= nNodes) tree(nodeIndex).right = -1
      if (tree(nodeIndex).left >= nNodes) tree(nodeIndex).left = -1
      tree(nodeIndex).purity = purity
    }
  }

  /** Counts the nodes which have been filled by the user */
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
      (for { node <- tree if (node.filled || true) } yield
        "Node " + node.nodeIndex +
        ", decides on feature " + node.featureIndex +
        (if (node.greater) "> " else "< ") + "%+.3f".format(node.threshold) +
        ", parent " + node.parent +
        ", purity %.3e".format(node.purity) +
        " left child " + node.left + " right child " + node.right + "\n"
      ).mkString

    "------- Decision Tree -------\n" +
    "Tree complete with " + nFilledNodes() + " / " + nNodes + " filled nodes\n" +
    printNodes +
    "------------------------------"
  }

  /**
   * Classifies an instance based on its feature vector
   * @param instance Feature list of an instance
   * @return Predicted label
   */
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

  /**
   * Returns the data (instances and labels) present at this node
   * @param nodeIndex The node index
   * @param X List of instances
   * @param y List of labels
   * @return List of instances and list of labels, both being a subset of the input X and y
   */
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
