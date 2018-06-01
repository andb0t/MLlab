package clustering

import scala.collection.mutable.ListBuffer

import play.api.libs.json.JsValue

import algorithms._
import json._



/** Companion object providing default parameters */
object SelfOrganizingMapClustering {
  val width: Int = 3
  val height: Int = 5
  val alpha: Double = 0.2
  val lambda: Int = -1
}

/** Self-organizing map clustering
 @param width width (in number of nodes) of the SOM
 @param height height (in number of nodes) of the SOM
 @param alpha size of the update step. Ranges from 0 (no update) to 1 (set equal to training instance)
 @param lambda number of iterations for random training. Default -1: run once and in order through training data
 */
 class SelfOrganizingMapClustering(
   width: Int = SelfOrganizingMapClustering.width,
   height: Int = SelfOrganizingMapClustering.height,
   alpha: Double = SelfOrganizingMapClustering.alpha,
   lambda: Int = SelfOrganizingMapClustering.lambda
 ) extends Clustering {
   def this(json: JsValue) = {
     this(
       width = JsonMagic.toInt(json, "width", SelfOrganizingMapClustering.width),
       height = JsonMagic.toInt(json, "height", SelfOrganizingMapClustering.height),
       alpha = JsonMagic.toDouble(json, "alpha", SelfOrganizingMapClustering.alpha),
       lambda = JsonMagic.toInt(json, "lambda", SelfOrganizingMapClustering.lambda)
       )
   }

   val name: String = "SelfOrganizingMapClustering"

   require(alpha >= 0 && alpha <= 1, "hyper parameter alpha has to be in interval [0, 1]")

   val SOM = new SelfOrganizingMap(height, width, alpha)

   def clusterMeans(): List[List[List[Double]]] =
     List(SOM.getMap()).transpose

   def train(X: List[List[Double]]): Unit = {
     SOM.initialize(X)
     if (lambda == -1) {
       for (instance <- X) SOM.update(instance)
     }else{
       val nInstances = X.length
       for (_ <- 0 until lambda) SOM.update(X(scala.util.Random.nextInt(nInstances)))
     }
   }

   def predict(X: List[List[Double]]): List[Int] =
     for (instance <- X) yield SOM.classifiy(instance)

}
