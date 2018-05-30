package clustering

import play.api.libs.json.JsValue
import scala.collection.mutable.ListBuffer

import algorithms._
import json._



/** Companion object providing default parameters */
object SelfOrganizingMapClustering {
  val width: Int = 10
  val height: Int = 10
}

/** Self-organizing map clustering
 */
 class SelfOrganizingMapClustering(
   width: Int = SelfOrganizingMapClustering.width,
   height: Int = SelfOrganizingMapClustering.height
 ) extends Clustering {
   def this(json: JsValue) = {
     this(
       width = JsonMagic.toInt(json, "width", SelfOrganizingMapClustering.width),
       height = JsonMagic.toInt(json, "height", SelfOrganizingMapClustering.height)
       )
   }

   val name: String = "SelfOrganizingMapClustering"

  var centroidEvolution = new ListBuffer[List[List[Double]]]()

  def clusterMeans(): List[List[List[Double]]] =
    centroidEvolution.toList.transpose

  def train(X: List[List[Double]], y: List[Int]): Unit =
    require(X.length == y.length, "both arguments must have the same length")

  def predict(X: List[List[Double]]): List[Int] = {
    val result = for (instance <- X) yield (Math.random * width * height).toInt
    centroidEvolution += kMeans.getCentroids(X, result, width * height)
    result
  }

}
