package algorithms

import breeze.linalg._

import utils._


class SelfOrganizingMap(height: Int, width: Int) {

  var nodes: List[DenseVector[Double]] = Nil

  println("Initializing SOM: " + this)

  def initialize(X: List[List[Double]]): Unit = {
    val nFeatures = X.head.length
    nodes = List.fill(height * width)(DenseVector.fill(nFeatures){scala.util.Random.nextDouble - 0.5})
  }

  def update(x: List[Double]): Unit = {
  }

  def classifiy(x: List[Double]): Int =
    0

  def getMap(): List[List[Double]] =
    nodes.map(Trafo.toList(_))

  override def toString(): String = {
    "SOM(%d x %d)".format(height, width)
  }
}
