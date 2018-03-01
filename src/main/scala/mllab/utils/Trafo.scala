package utils

import breeze.linalg._


object Trafo {

  def toMatrix(X: List[List[Double]]): DenseMatrix[Double] =
    DenseMatrix(X.flatten).reshape(X.head.length, X.length).t

  def toVector(y: List[Double]): DenseVector[Double] =
    DenseVector(y.toArray)

  def toVectorInt(y: List[Int]): DenseVector[Int] =  // breeze doesn't allow polymorphism?
    convert(toVector(y.map(_.toDouble)), Int)

}
