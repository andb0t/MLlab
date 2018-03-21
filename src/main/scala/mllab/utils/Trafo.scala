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

    /** Transform 1D list to 1D vector of integers */
  def toVectorInt(y: List[Int]): DenseVector[Int] =  // breeze doesn't allow polymorphism?
    convert(toVector(y.map(_.toDouble)), Int)

}
