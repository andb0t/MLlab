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

  val hier = new Hierarchical(k)

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
