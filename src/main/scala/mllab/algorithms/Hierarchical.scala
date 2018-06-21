package algorithms

import breeze.linalg._

import utils._


/** A class representing a hierarchical clustering
  *
  * @constructor Create a new hierarchical clustering object
  * @param parameters See clustering implementation
  */
class Hierarchical(k: Int) {

  /** Determines the cluster association
   * @param X List of instances to cluster
   */
  def classifiy(X: List[List[Double]]): List[Int] = {
    val result = List.fill(X.length)(scala.util.Random.nextInt(3))
    result
  }

  /** Get cluster means */
  def getClusterMeans(): List[List[Double]] = {
    List.fill(3)(List(scala.util.Random.nextDouble, 0))
  }

  override def toString(): String = {
    "Hierarchical k=%d".format(k)
  }

}
