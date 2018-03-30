package clustering

import utils._


/** k-Means clustering
 * @param k Number of clusters to search for
 * @todo improve centroid initialization
 */
class kMeansClustering(k: Int = 3) extends Clustering {

  val name: String = "kMeansClustering"

  def predict(X: List[List[Double]]): List[Int] = {
    val nFeatures = X.head.length
    val range = 2
    val centroids: List[List[Double]] = List.fill(k)(List.fill(nFeatures)((scala.util.Random.nextDouble - 0.5) * range))
    val maxIter = 100

    def getLoss(X: List[List[Double]], y: List[Int], centroids: List[List[Double]]): Double = {
      0.0
    }

    def updateCentroids(X: List[List[Double]], y: List[Int], k: Int): List[List[Double]] = {
      (for (c <- 0 until k) yield {
        val thisClusterX = (X zip y).filter(_._2 == c).map(_._1)
        val ThisClusterXLength = thisClusterX.length
        thisClusterX.reduce((x0, x1) => Maths.plus(x0, x1).map(_ / ThisClusterXLength))
      }).toList
    }

    def clusterToCentroid(count: Int, X: List[List[Double]], y: List[Int], centroids: List[List[Double]], stop: Boolean): Tuple2[List[Int], List[List[Double]]] = {
      if (count >= maxIter || stop) {
        val loss = getLoss(X, y, centroids)
        println("Final% 4d with loss %.4e and centroids ".format(count, loss) +
          centroids.map(c => c.map(Maths.round(_, 3)).mkString("[", ", ", "]")))
        Tuple2(y, centroids)
      }
      else {
        if (count % 100 == 0 || (count < 50 && count % 10 == 0) || (count < 5)) {
          val loss = getLoss(X, y, centroids)
          println("Step% 4d with loss %.4e and centroids ".format(count, loss) +
            centroids.map(c => c.map(Maths.round(_, 3)).mkString("[", ", ", "]")))
        }
        val newy: List[Int] = X.map(x => (for (i <- 0 until centroids.length) yield Maths.distance(centroids(i), x)).zipWithIndex.min._2)
        val newCentroids: List[List[Double]] = updateCentroids(X, newy, k)
        if (newCentroids.toSet != centroids.toSet)
          clusterToCentroid(count+1, X, newy, newCentroids, stop)
        else
          clusterToCentroid(count+1, X, newy, centroids, true)
      }
    }
    clusterToCentroid(0, X, Nil, centroids, false)._1
  }

}
