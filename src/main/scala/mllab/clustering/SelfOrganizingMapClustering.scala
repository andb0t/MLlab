package clustering

import scala.collection.mutable.ListBuffer

import play.api.libs.json.JsValue

import algorithms._
import json._



/** Companion object providing default parameters */
object SelfOrganizingMapClustering {
  val width: Int = 2
  val height: Int = 2
  val alpha: Double = 0.2
  val alphaHalflife: Int = 1000
  val alphaDecay: String = "exp"
  val lambda: Int = -1
  val initStrat: String = "random"
}

/** Self-organizing map clustering
 * @param width Width (in number of nodes) of the SOM
 * @param height Height (in number of nodes) of the SOM
 * @param alpha Learning rate = size of the update step. Ranges from 0 (no update) to 1 (set equal to training instance)
 * @param alphaHalflife Learning rate decay after this number of training steps
 * @param alphaDecay Type of learning rate decay
 * @param lambda Number of iterations for random training. Default -1: run once and in order through training data
 */
 class SelfOrganizingMapClustering(
   width: Int = SelfOrganizingMapClustering.width,
   height: Int = SelfOrganizingMapClustering.height,
   alpha: Double = SelfOrganizingMapClustering.alpha,
   alphaHalflife: Int = SelfOrganizingMapClustering.alphaHalflife,
   alphaDecay: String = SelfOrganizingMapClustering.alphaDecay,
   lambda: Int = SelfOrganizingMapClustering.lambda,
   initStrat: String = SelfOrganizingMapClustering.initStrat
 ) extends Clustering {
   def this(json: JsValue) = {
     this(
       width = JsonMagic.toInt(json, "width", SelfOrganizingMapClustering.width),
       height = JsonMagic.toInt(json, "height", SelfOrganizingMapClustering.height),
       alpha = JsonMagic.toDouble(json, "alpha", SelfOrganizingMapClustering.alpha),
       alphaHalflife = JsonMagic.toInt(json, "alphaHalflife", SelfOrganizingMapClustering.alphaHalflife),
       alphaDecay = JsonMagic.toString(json, "alphaDecay", SelfOrganizingMapClustering.alphaDecay),
       lambda = JsonMagic.toInt(json, "lambda", SelfOrganizingMapClustering.lambda),
       initStrat = JsonMagic.toString(json, "initStrat", SelfOrganizingMapClustering.initStrat)
       )
   }

   val name: String = "SelfOrganizingMapClustering"

   require(alpha >= 0 && alpha <= 1, "hyper parameter alpha has to be in interval [0, 1]")

   val SOM = new SelfOrganizingMap(height, width, alpha, alphaHalflife, alphaDecay, initStrat)

   def clusterMeans(): List[List[List[Double]]] =
     // List(SOM.getCurrentMap()).transpose
     SOM.nodesHistory.reverse.transpose

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
