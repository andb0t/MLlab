package classifiers

import breeze.linalg._


abstract class Classifier() {
  // val X: DenseMatrix[Double] = DenseMatrix(Xraw.flatten).reshape(inputLayer, Xraw.length).t
  // val y: DenseVector[Int] = DenseVector(yraw.toArray)
  def train(X: List[List[Double]], y: List[Int]): Unit
  def predict(X: List[List[Double]]): List[Int]
}

abstract class ClassifierBreeze() {
  def train(X: DenseMatrix[Double], y: DenseVector[Int]): Unit
  def predict(X: DenseMatrix[Double]): DenseVector[Int]
}
