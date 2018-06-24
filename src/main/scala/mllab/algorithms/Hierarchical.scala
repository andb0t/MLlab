package algorithms

import breeze.linalg._

import utils._


/** A class representing a hierarchical clustering
  *
  * @constructor Create a new hierarchical clustering object
  * @param parameters See clustering implementation
  */
class Hierarchical(k: Int, metric: String) {

  var clusterMeans: List[List[Double]] = Nil

  def distance(a: List[Double], b: List[Double]): Double =
    if (metric == "euclidian") Maths.distance(a, b)
    else if (metric == "L1") Maths.distanceL1(a, b)
    else throw new NotImplementedError("metric " + metric + " not implemented")

  /** Determines the cluster association
   * @param X List of instances to cluster
   */
  def classifiy(X: List[List[Double]]): List[Int] = {
    val shuffledX: List[(List[Double], Int)] = scala.util.Random.shuffle(X.zipWithIndex)

    def clusterBatch(clusterX: List[(List[Double], Int)], clustering: List[Tuple2[Int, Int]], step: Int): List[Tuple2[Int, Int]] = {
      val nBatch = min(clusterX.length, 100)
      if (nBatch > k) {
        val distMatrix: DenseMatrix[Double] = DenseMatrix.tabulate(nBatch, nBatch){
          case (i, j) =>
          if (i > j) distance(clusterX(i)._1, clusterX(j)._1)
          else Double.MaxValue
        }
        val (iMin, jMin) = argmin(distMatrix)
        val (iInst, jInst) = (clusterX(iMin), clusterX(jMin))
        val newFeatureVals = Maths.plus(iInst._1, jInst._1).map(_ / 2)
        val newElement: Tuple2[List[Double], Int] = (newFeatureVals, iInst._2)
        val newClusterX = newElement :: Trafo.dropElement(clusterX, List(iMin, jMin))
        val newClustering = (jInst._2, iInst._2) :: clustering
        // println(s"Step $step:")
        // println("Merge instance " + iInst)
        // println("and instance   " + jInst)
        // println("into           " + newElement)
        // println("clusterX       " + clusterX.take(7))
        // println("newClusterX    " + newClusterX.take(7))
        // println("newClustering  " + newClustering)
        clusterBatch(newClusterX, newClustering, step + 1)
      }
      else clustering
    }

    val merges = clusterBatch(shuffledX, Nil, 0)
    println("Determined merges: " + merges.length)

    def applyMerges(merges: List[Tuple2[Int, Int]], clustering: List[Int]): List[Int] = merges match {
      case Nil => clustering
      case merge::rest => applyMerges(rest, clustering.map{case x => if (x == merge._1) merge._2 else x})
    }

    val originalClustering = applyMerges(merges.reverse, List.range(0, X.length))
    val clusterIndices = originalClustering.toSet.toList.zipWithIndex
    val clustering = applyMerges(clusterIndices, originalClustering)
    clusterMeans = kMeans.getCentroids(X, clustering, k)

    println("Clustered instances: " + clustering.length)
    println("Unique labels: " + clustering.toSet.size)
    clustering

  }

  /** Get cluster means */
  def getClusterMeans(): List[List[Double]] = {
    clusterMeans
  }

  override def toString(): String = {
    "Hierarchical clustering: k=%d, %s metric".format(k, metric)
  }

}
