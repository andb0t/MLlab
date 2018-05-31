package algorithms

import breeze.linalg._

import utils._


class SelfOrganizingMap(height: Int, width: Int, alpha: Double) {

  var nodes: List[DenseVector[Double]] = Nil

  def getNeighbors(index: Int, height: Int, width: Int): List[Int] = {
    val vertical = List(index - width, index + width).filter(_ >= 0).filter(_ < height * width)
    val horizontal = Nil
    vertical ::: horizontal
  }

  def initialize(X: List[List[Double]]): Unit = {
    val nFeatures = X.head.length
    val features = X.transpose
    val mins = features.map(_.min)
    val maxs = features.map(_.max)
    println("Initializing SOM:")
    for (i <- 0 until nFeatures)
      println("Feature %d range: %.3f - %.3f".format(i, mins(i), maxs(i)))
    nodes = (for (i <- 0 until height * width) yield
      DenseVector.tabulate(nFeatures){i => mins(i) + scala.util.Random.nextDouble * (maxs(i) - mins(i))}
    ).toList
    println("Initial nodes:")
    for (i <- 0 until nodes.length) println(s"$i : " + nodes(i))
    println("Initialized SOM: " + this)
  }

  def update(x: List[Double]): Unit = {
    val index = classifiy(x)
    nodes(index) += Trafo.toVector(x)
    nodes(index) *= alpha
    val neighbors = getNeighbors(index, height, width)
    for (index <- neighbors) {
      nodes(index) += Trafo.toVector(x)
      nodes(index) *= alpha * 0.5

    }
    println("Updated nodes:")
    for (i <- List(index) ::: neighbors) println(s"$i : " + nodes(i))
    println()
  }

  def classifiy(x: List[Double]): Int = {
    val distances: List[Double] = nodes.map{n => norm(n - Trafo.toVector(x))}
    distances.zipWithIndex.minBy(_._1)._2
  }

  def getMap(): List[List[Double]] =
    nodes.map(Trafo.toList(_))

  override def toString(): String = {
    "SOM(%d x %d x %d)".format(height, width, nodes.head.length)
  }
}
