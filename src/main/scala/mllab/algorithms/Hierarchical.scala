package algorithms

import breeze.linalg._

import utils._


/** A class representing a hierarchical clustering
  *
  * @constructor Create a new hierarchical clustering object
  * @param parameters See clustering implementation
  */
class Hierarchical(k: Int) {

  var clusterMeans: List[List[Double]] = Nil

  /** Determines the cluster association
   * @param X List of instances to cluster
   */
  def classifiy(X: List[List[Double]]): List[Int] = {
    val shuffledX: List[(List[Double], Int)] = scala.util.Random.shuffle(X.take(7).zipWithIndex)

    def clusterBatch(clusterX: List[(List[Double], Int)], clustering: List[Tuple2[Int, Int]], step: Int): List[Tuple2[Int, Int]] = {
      val nBatch = min(clusterX.length, 6)
      if (nBatch > k && step < 5) {
        val distMatrix: DenseMatrix[Double] = DenseMatrix.tabulate(nBatch, nBatch){
          case (i, j) =>
          if (i > j) Maths.distance(clusterX(i)._1, clusterX(j)._1)
          else Double.MaxValue
        }
        val (iMin, jMin) = argmin(distMatrix)
        val (iInst, jInst) = (clusterX(iMin), clusterX(jMin))
        val newFeatureVals = Maths.plus(iInst._1, jInst._1).map(_ / 2)
        val newElement: Tuple2[List[Double], Int] = (newFeatureVals, iInst._2)
        val newClusterX = newElement :: Trafo.dropElement(clusterX, List(iMin, jMin))
        val newClustering = (jInst._2, iInst._2) :: clustering
        println(s"Step $step:")
        println("Merge instance " + iInst)
        println("and instance   " + jInst)
        println("into           " + newElement)
        println("clusterX       " + clusterX.take(7))
        println("newClusterX    " + newClusterX.take(7))
        println("newClustering  " + newClustering)
        clusterBatch(newClusterX, newClustering, step + 1)
      }
      else clustering
    }

    val newClustering = clusterBatch(shuffledX, Nil, 0)
    println(newClustering.take(7))

    val clustering = List.fill(X.length)(scala.util.Random.nextInt(3))
    clusterMeans = kMeans.getCentroids(X, clustering, k)
    clustering
  }

  /** Get cluster means */
  def getClusterMeans(): List[List[Double]] = {
    clusterMeans
  }

  override def toString(): String = {
    "Hierarchical k=%d".format(k)
  }

}
