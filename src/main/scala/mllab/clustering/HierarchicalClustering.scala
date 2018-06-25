package clustering

import play.api.libs.json.JsValue

import algorithms._
import json._


/** Companion object providing default parameters */
object HierarchicalClustering {
  val k: Int = 3
  val metric: String = "euclidian"
}

/** Hierarchical clustering
 * @param k Desired number of clusters
 * @param metric Desired distance metric for clustering
 */
class HierarchicalClustering(
  k: Int = HierarchicalClustering.k,
  metric: String = HierarchicalClustering.metric
) extends Clustering {
  def this(json: JsValue) = {
    this(
      k = JsonMagic.toInt(json, "k", HierarchicalClustering.k),
      metric = JsonMagic.toString(json, "metric", HierarchicalClustering.metric)
      )
  }

  val name: String = "HierarchicalClustering"

  val hier = new Hierarchical(k, metric)

  def clusterMeans(): List[List[List[Double]]] =
    List(hier.getClusterMeans).transpose

  def train(X: List[List[Double]]): Unit = {
    println("No training necessary")
  }

  def predict(X: List[List[Double]]): List[Int] = {
    val result = hier.classifiy(X)
    println(hier)
    result
  }

}
