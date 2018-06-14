package clustering

import play.api.libs.json.JsValue

import algorithms._
import json._


/** Companion object providing default parameters */
object HierarchicalClustering {
  val k: Int = 3
}

/** Hierarchical clustering
 * @param k Number of clusters to stop clustering
 * @todo improve centroid initialization
 */
class HierarchicalClustering(
  k: Int = HierarchicalClustering.k
) extends Clustering {
  def this(json: JsValue) = {
    this(
      k = JsonMagic.toInt(json, "k", HierarchicalClustering.k)
      )
  }

  val name: String = "HierarchicalClustering"

  var centroidEvolution: List[List[List[Double]]] = Nil

  def clusterMeans(): List[List[List[Double]]] =
    centroidEvolution.transpose

  def train(X: List[List[Double]]): Unit = {
    val clustering = for (instance <- X) yield (Math.random * k).toInt
    centroidEvolution = List(kMeans.getCentroids(X, clustering, k))
  }

  def predict(X: List[List[Double]]): List[Int] =
    for (instance <- X) yield (Math.random * k).toInt

}
