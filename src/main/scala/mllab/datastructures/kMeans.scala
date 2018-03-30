package datastructures

import utils._


/** Provides functions for the k-means clustering algorithm */
object kMeans {

  def getCentroids(X: List[List[Double]], y: List[Int], k: Int): List[List[Double]] = {
    (for (c <- 0 until k) yield {
      val thisClusterX = (X zip y).filter(_._2 == c).map(_._1)
      val ThisClusterXLength = thisClusterX.length
      val centroid: List[Double] = thisClusterX.reduce((x0, x1) => Maths.plus(x0, x1)).map(_ / ThisClusterXLength)
      // println(s"ELements in cluster $c: $ThisClusterXLength with position " + centroid)
      centroid
    }).toList
  }
}
