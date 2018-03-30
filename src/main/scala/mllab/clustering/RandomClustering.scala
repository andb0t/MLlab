package clustering

import scala.collection.mutable.ListBuffer

import datastructures._


/** Random clustering
 *
 * This is a clustering algorithm deciding randomly on the output class
 */
class RandomClustering() extends Clustering {

  val name: String = "RandomClustering"

  var centroidEvolution = new ListBuffer[List[List[Double]]]()

  var k: Int = 3

  def clusterMeans(): List[List[List[Double]]] =
    centroidEvolution.toList.transpose

  def predict(X: List[List[Double]]): List[Int] = {
    val result = for (instance <- X) yield (Math.random * k).toInt
    centroidEvolution += kMeans.getCentroids(X, result, k)
    result
  }

}
