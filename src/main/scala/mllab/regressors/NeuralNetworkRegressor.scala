package regressors

import scala.collection.mutable.ListBuffer

import breeze.linalg._

import utils._
import datastructures._


/** Neural network regressor
 *
 */
class NeuralNetworkRegressor(
    activation: String = "tanh",
    layers: List[Int] = List(1, 4, 1)
) extends Regressor {

  require(layers.length > 2, "too few layers: need at least an input, a middle and an output layer")

  val name: String = "NeuralNetworkRegressor"

  val W: IndexedSeq[DenseMatrix[Double]] = for (i <- 0 until layers.length - 1) yield DenseMatrix.rand[Double](layers(i),layers(i+1))
  val b: IndexedSeq[DenseVector[Double]] = for (i <- 0 until layers.length - 1) yield DenseVector.zeros[Double](layers(i+1))

  def train(listX: List[List[Double]], listy: List[Double]): Unit =
    require(listX.length == listy.length, "both arguments must have the same length")

  def predict(listX: List[List[Double]]): List[Double] = {
    val X: DenseMatrix[Double] = Trafo.toMatrix(listX)
    val output = NeuralNetwork.feedForward(X, W, b, activation)
    val prediction: DenseVector[Double] = output(::, 0)
    (for (i <- 0 until prediction.size) yield prediction(i)).toList
  }

}
