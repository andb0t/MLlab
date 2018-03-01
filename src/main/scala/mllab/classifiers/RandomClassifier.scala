package classifiers

import breeze.linalg._


class RandomClassifier() extends ClassifierBreeze {

  def train(X: DenseMatrix[Double], y: DenseVector[Int]): Unit =
    require(X.rows == y.size, "number of training instances and labels is not equal")

  def predict(X: DenseMatrix[Double]): DenseVector[Int] =
    DenseVector.tabulate(X.rows){i => if (Math.random < 0.5) 0 else 1}

}
