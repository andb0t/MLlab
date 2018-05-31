package clustering

import scala.collection.mutable.ListBuffer

import algorithms._


/** Random clustering
 *
 * This is a clustering algorithm deciding randomly on the output class
 */
class RandomClustering() extends Clustering {

  val name: String = "RandomClustering"

  var centroidEvolution: List[List[List[Double]]] = Nil

  var k: Int = 3

  def clusterMeans(): List[List[List[Double]]] =
    centroidEvolution.transpose

  def train(X: List[List[Double]]): Unit = {
    val clustering = for (instance <- X) yield (Math.random * k).toInt
    centroidEvolution = List(kMeans.getCentroids(X, clustering, k))
  }

  def predict(X: List[List[Double]]): List[Int] =
    for (instance <- X) yield (Math.random * k).toInt

}
