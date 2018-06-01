package algorithms

import breeze.linalg._

import utils._


/** A class representing a self-organizing map (SOM)
  *
  * @constructor Create a new SOM
  * @param parameters See clustering implementation
  */
class SelfOrganizingMap(height: Int, width: Int, alpha: Double,  alphaHalflife: Int, alphaDecay: String, initStrat: String = "random") {

  var nodes: List[DenseVector[Double]] = Nil
  var nodesHistory: List[List[List[Double]]] = Nil
  var count: Int = 0

  /** Determines neighboring nodes
   * @param index Index of the node
   * @param height Height of the node mesh
   * @param width Width of the node mesh
   */
  def getNeighbors(index: Int, height: Int, width: Int): List[Int] = {
    def rowNeighbors(index: Int, width: Int): List[Int] = {
      val idx = index % width
      if (idx == 0 && idx != width - 1) List(index + 1)
      else if (idx != 0 && idx == width - 1) List(index - 1)
      else List(index - 1, index + 1)
    }
    val vertical = List(index - width, index + width).filter(_ >= 0).filter(_ < height * width)
    val horizontal = rowNeighbors(index, width)
    val diagonal = vertical.flatMap(rowNeighbors(_, width))
    vertical ::: horizontal ::: diagonal
  }


  /** Initializes the SOM
   * @param X List of training instances
   */
  def initialize(X: List[List[Double]]): Unit = {
    val nFeatures = X.head.length
    val features = X.transpose

    if (initStrat == "random") {
      val mins = features.map(_.min)
      val maxs = features.map(_.max)
      println("Initializing SOM:")
      for (i <- 0 until nFeatures)
      println("Feature %d range: %.3f - %.3f".format(i, mins(i), maxs(i)))
      nodes = (for (i <- 0 until height * width) yield
        DenseVector.tabulate(nFeatures){i => mins(i) + scala.util.Random.nextDouble * (maxs(i) - mins(i))}
      ).toList
    }
    else if (initStrat == "PCA") {
      val featureMatrix = Trafo.toMatrix(features.transpose)
      def eigen = (p: PCA) => (p.loadings, p.eigenvalues)
      val (eigenvectors, eigenvalues) = eigen(princomp(featureMatrix))
      println("eigenvalues " + eigenvalues)
      println("eigenvectors " + eigenvectors)
    }
    else throw new NotImplementedError(s"Initialization strategy $initStrat not implemented!")

    println("Initial nodes:")
    for (i <- 0 until nodes.length) println(s"$i : " + nodes(i))
    println("Initialized SOM: " + this)
    nodesHistory = getCurrentMap :: nodesHistory
  }

  /** Performs one training step of the SOM
   * @param x Current training instance
   */
  def update(x: List[Double]): Unit = {
    val decayedAlpha: Double =
      if (alphaDecay == "step") alpha / Math.pow(2, Math.floor(count.toFloat / alphaHalflife))
      else if (alphaDecay == "exp") alpha * Math.exp(-1.0 * count / alphaHalflife)
      else alpha

    val index = classifiy(x)
    nodes(index) *= (1 - decayedAlpha)
    nodes(index) += Trafo.toVector(x) * decayedAlpha
    val neighbors = getNeighbors(index, height, width)
    for (index <- neighbors) {
      nodes(index) *= (1 - decayedAlpha * 0.5)
      nodes(index) += Trafo.toVector(x) * decayedAlpha * 0.5
    }
    val updatedNodes = List(index) ::: neighbors
    // println("%d. nodes update with alpha %.3f and instance ".format(count, decayedAlpha) + x + ":")
    // for (i <- updatedNodes) println(s"  $i : " + nodes(i))
    count += 1
    if (count % 100 == 0 || (count < 50 && count % 10 == 0) || (count < 5))
      nodesHistory = getCurrentMap :: nodesHistory
  }

  /** Determines the best matcvhing unit (BMU) for an instance
   * @param x Current training instance
   */
  def classifiy(x: List[Double]): Int = {
    val distances: List[Double] = nodes.map{n => norm(n - Trafo.toVector(x))}
    distances.zipWithIndex.minBy(_._1)._2
  }

  /** Returns the current SOM */
  def getCurrentMap(): List[List[Double]] =
    nodes.map(Trafo.toList(_))

  override def toString(): String = {
    "SOM(%d x %d x %d)".format(height, width, nodes.head.length)
  }
}
