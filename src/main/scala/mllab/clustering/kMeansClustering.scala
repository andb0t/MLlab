package clustering

import scala.collection.mutable.ListBuffer

import play.api.libs.json.JsValue

import algorithms._
import json._
import utils._


/** Companion object providing default parameters */
object kMeansClustering {
  val k: Int = 3
}

/** k-Means clustering
 * @param k Number of clusters to search for
 * @todo improve centroid initialization
 */
class kMeansClustering(
  k: Int = kMeansClustering.k
) extends Clustering {
  def this(json: JsValue) = {
    this(
      k = JsonMagic.toInt(json, "k", kMeansClustering.k)
      )
  }

  val name: String = "kMeansClustering"

  var lossEvolution = new ListBuffer[(Double, Double)]()
  var centroidEvolution: List[List[List[Double]]] = Nil

  def clusterMeans(): List[List[List[Double]]] =
    centroidEvolution.transpose

  def refineCentroids(count: Int, X: List[List[Double]], y: List[Int], centroids: List[List[List[Double]]], stop: Boolean, maxIter: Int): List[List[List[Double]]] = {
    if (count >= maxIter || stop) {
      val loss = kMeans.getLoss(X, y, centroids.head)
      lossEvolution += Tuple2(count.toDouble, loss)
      println("Final% 4d with loss %.4e".format(count, loss))
      if (stop) println("Stable means reached!")
      else if (count >= maxIter) println(s"Maximum number of iterations ($maxIter) reached!")
      centroids.reverse
    }
    else {
      val newy: List[Int] = kMeans.cluster(X, centroids.head)
      if (count % 100 == 0 || (count < 50 && count % 10 == 0) || (count < 5)) {
        val loss = kMeans.getLoss(X, newy, centroids.head)
        lossEvolution += Tuple2(count.toDouble, loss)
        println("Step% 5d with loss %.4e".format(count, loss))
      }
      val newCentroids: List[List[Double]] = kMeans.getCentroids(X, newy, k)
      if (newCentroids.toSet != centroids.head.toSet)
        refineCentroids(count+1, X, newy, newCentroids :: centroids, stop, maxIter)
      else
        refineCentroids(count+1, X, newy, centroids, true, maxIter)
    }
  }

  def train(X: List[List[Double]]): Unit = {
    val nFeatures = X.head.length
    val range = 2
    val centroids: List[List[Double]] = List.fill(k)(List.fill(nFeatures)((scala.util.Random.nextDouble - 0.5) * range))
    val maxIter = 100
    centroidEvolution = refineCentroids(0, X, Nil, List(centroids), false, maxIter)
  }

  def predict(X: List[List[Double]]): List[Int] =
    kMeans.cluster(X, centroidEvolution.last)

  override def diagnostics(): Map[String, List[(Double, Double)]] = {
    Map("loss" -> lossEvolution.toList)
  }

}
