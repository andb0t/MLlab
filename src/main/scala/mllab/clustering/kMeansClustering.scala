package clustering

import play.api.libs.json.JsValue
import scala.collection.mutable.ListBuffer

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
  var centroidEvolution = new ListBuffer[List[List[Double]]]()

  def clusterMeans(): List[List[List[Double]]] =
    centroidEvolution.toList.transpose

  def predict(X: List[List[Double]]): List[Int] = {
    val nFeatures = X.head.length
    val range = 2
    val centroids: List[List[Double]] = List.fill(k)(List.fill(nFeatures)((scala.util.Random.nextDouble - 0.5) * range))
    val maxIter = 100

    def clusterToCentroid(count: Int, X: List[List[Double]], y: List[Int], centroids: List[List[Double]], stop: Boolean): Tuple2[List[Int], List[List[Double]]] = {
      if (count >= maxIter || stop) {
        val loss = kMeans.getLoss(X, y, centroids)
        lossEvolution += Tuple2(count.toDouble, loss)
        centroidEvolution += centroids
        println("Final% 4d with loss %.4e".format(count, loss))
        if (stop) println("Stable means reached!")
        else if (count >= maxIter) println(s"Maximum number of iterations ($maxIter) reached!")
        Tuple2(y, centroids)
      }
      else {
        val newy: List[Int] = X.map(x => (for (centroid <- centroids) yield Maths.distance(centroid, x)).zipWithIndex.min._2)
        if (count % 100 == 0 || (count < 50 && count % 10 == 0) || (count < 5)) {
          val loss = kMeans.getLoss(X, newy, centroids)
          lossEvolution += Tuple2(count.toDouble, loss)
          println("Step% 5d with loss %.4e".format(count, loss))
        }
        centroidEvolution += centroids
        val newCentroids: List[List[Double]] = kMeans.getCentroids(X, newy, k)
        if (newCentroids.toSet != centroids.toSet)
          clusterToCentroid(count+1, X, newy, newCentroids, stop)
        else
          clusterToCentroid(count+1, X, newy, centroids, true)
      }
    }
    clusterToCentroid(0, X, Nil, centroids, false)._1
  }

  override def diagnostics(): Map[String, List[(Double, Double)]] = {
    Map("loss" -> lossEvolution.toList)
  }

}
