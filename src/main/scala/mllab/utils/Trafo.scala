package utils

import breeze.linalg._


/** Provides auxiliary functions for list to tensor transformation */
object Trafo {

  /** Transform 2D list to 2D matrix */
  def toMatrix(X: List[List[Double]]): DenseMatrix[Double] =
    DenseMatrix(X.flatten).reshape(X.head.length, X.length).t

    /** Transform 1D list to 1D vector */
  def toVector(y: List[Double]): DenseVector[Double] =
    DenseVector(y.toArray)

    /** Transform 1D list to 1D vector of integers (breeze doesn't allow polymorphism?)  */
  def toVectorInt(y: List[Int]): DenseVector[Int] =
    convert(toVector(y.map(_.toDouble)), Int)

  /** Picks elements from a list according to a list of indices */
  def iloc[T](list: List[T], indices: List[Int], result: List[T]=Nil): List[T] = indices match {
    case Nil => result
    case index::rest => {
      iloc(list, rest, list(index)::result)
    }
  }

  /** Samples random instances from the given data */
  def randomizeInstancesInt(X: DenseMatrix[Double], y: DenseVector[Int], nInst: Int): Tuple2[DenseMatrix[Double], DenseVector[Int]] = {
    val randomIndices: Seq[Int] =
      if (nInst != -1)  Seq.fill(nInst)(scala.util.Random.nextInt(X.rows))
      else 0 until X.rows
    val randomX: DenseMatrix[Double] = X(randomIndices, ::).toDenseMatrix
    val randomy: DenseVector[Int] = y(randomIndices).toDenseVector
    Tuple2(randomX, randomy)
  }

  /** Samples random instances from the given data */
  def randomizeInstances(X: DenseMatrix[Double], y: DenseVector[Double], nInst: Int): Tuple2[DenseMatrix[Double], DenseVector[Double]] = {
    val randomIndices: Seq[Int] =
      if (nInst != -1)  Seq.fill(nInst)(scala.util.Random.nextInt(X.rows))
      else 0 until X.rows
    val randomX: DenseMatrix[Double] = X(randomIndices, ::).toDenseMatrix
    val randomy: DenseVector[Double] = y(randomIndices).toDenseVector
    Tuple2(randomX, randomy)
  }
}
