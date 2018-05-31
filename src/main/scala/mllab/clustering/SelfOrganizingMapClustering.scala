package clustering

import scala.collection.mutable.ListBuffer

import play.api.libs.json.JsValue

import algorithms._
import json._



/** Companion object providing default parameters */
object SelfOrganizingMapClustering {
  val width: Int = 3
  val height: Int = 5
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

   val SOM = new SelfOrganizingMap(height, width)

   def clusterMeans(): List[List[List[Double]]] =
     List(SOM.getMap()).transpose

   def train(X: List[List[Double]]): Unit = {
     SOM.initialize(X)
     for (instance <- X) SOM.update(instance)
   }

   def predict(X: List[List[Double]]): List[Int] =
     for (instance <- X) yield SOM.classifiy(instance)

}
