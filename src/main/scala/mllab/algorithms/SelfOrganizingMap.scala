package algorithms

import breeze.linalg._

import utils._


class SelfOrganizingMap(height: Int, width: Int) {

  var nodes: List[DenseVector[Double]] = Nil

  println("Initializing SOM: " + this)

  def update(x: List[Double]): Unit = {
    val nFeatures = x.length
    nodes = List.fill(height * width)(DenseVector.fill(nFeatures){scala.util.Random.nextDouble - 0.5})
  }

  def classifiy(x: List[Double]): Tuple2[List[Double], Int] = {
    val node = List(0.3, 0.3)
    val index = 0
    Tuple2(node, index)
  }

  def getMap(): List[List[Double]] =
    nodes.map(Trafo.toList(_))

  override def toString(): String = {
    "SOM(%d x %d)".format(height, width)
  }
}
