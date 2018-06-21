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
    List.fill(X.length)(0)
  }

  override def toString(): String = {
    "Hierarchical k=%d".format(k)
  }

}
