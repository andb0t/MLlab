package algorithms

import utils._


/** Provides functions for the k-means clustering algorithm */
object kMeans {

  /** Calculates the geometrical centers of the given clusters
   * @param X list of instances
   * @param y list of cluster association
   * @param k the total number of clusters
   */
  def getCentroids(X: List[List[Double]], y: List[Int], k: Int): List[List[Double]] = {
    (for (c <- 0 until k) yield {
      val cluster = (X zip y).filter(_._2 == c).map(_._1)
      val clusterLength = cluster.length
      val centroid: List[Double] =
        if (clusterLength > 1) cluster.reduce((x0, x1) => Maths.plus(x0, x1)).map(_ / clusterLength)
        else {
          val range = 2
          List.fill(X.head.length)((scala.util.Random.nextDouble - 0.5) * range)
        }
      // println(s"ELements in cluster $c: $clusterLength with position " + centroid)
      centroid
    }).toList
  }

  /** Clusters the input data according to given centroids
   * @param X list of instances
   * @param centroids coordinates of the centroids
   */
  def cluster(X: List[List[Double]], centroids: List[List[Double]]): List[Int] =
    X.map(x => (for (centroid <- centroids) yield Maths.distance(centroid, x)).zipWithIndex.min._2)

  /** Calculates the loss of the current clustering
   * @param X list of instances
   * @param y list of cluster association
   * @param centroids coordinates of the centroids
   */
  def getLoss(X: List[List[Double]], y: List[Int], centroids: List[List[Double]]): Double = {
    (for (c <- 0 until centroids.length) yield {
      val cluster = (X zip y).filter(_._2 == c).map(_._1)
      val clusterSquaredDist: List[List[Double]] = cluster.map(x => Maths.minus(x, centroids(c)).map(Math.pow(_, 2)))
      val clusterLoss: Double = clusterSquaredDist.map(_.sum).sum
      clusterLoss
    }).sum
  }

}
