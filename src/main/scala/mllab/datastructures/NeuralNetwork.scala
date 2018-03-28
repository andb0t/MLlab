package datastructures

import breeze.linalg._
import breeze.numerics._


object NeuralNetwork {

  def neuronTrafo(X: DenseMatrix[Double], W: DenseMatrix[Double], b: DenseVector[Double]): DenseMatrix[Double] =
    X * W + DenseVector.ones[Double](X.rows) * b.t

  def activate(Z: DenseMatrix[Double], activation: String): DenseMatrix[Double] =
    if (activation == "tanh") tanh(Z)
    else if (activation == "logistic") 1.0 / (exp(-Z) + 1.0)
    else if (activation == "identity") 0.01 * Z  // TODO: fix it: NaN in training if not scaled down
    else if (activation == "RELU") I(Z :> 0.0) *:* Z
    else if (activation == "leakyRELU") (I(Z :> 0.0) + 0.1 * I(Z :<= 0.0)) *:* Z
    else if (activation == "perceptron") I(Z :> 0.0)
    else throw new Exception("activation function not implented")

  def derivActivate(A: DenseMatrix[Double], activation: String): DenseMatrix[Double] =
    if (activation == "tanh") 1.0 - pow(A, 2)
    else if (activation == "logistic") A *:* (1.0 - A)
    else if (activation == "identity") DenseMatrix.ones[Double](A.rows, A.cols)
    else if (activation == "RELU") I(A :> 0.0)
    else if (activation == "leakyRELU") I(A :> 0.0) + 0.1 * I(A :<= 0.0)
    else if (activation == "perceptron") DenseMatrix.zeros[Double](A.rows, A.cols)
    else throw new Exception("activation function not implented")

  def feedForward(X: DenseMatrix[Double], W: IndexedSeq[DenseMatrix[Double]], b: IndexedSeq[DenseVector[Double]], activation: String): DenseMatrix[Double] = {
    def applyLayer(X: DenseMatrix[Double], count: Int): DenseMatrix[Double] =
      if (count < b.length - 1) {
        val inputZ = NeuralNetwork.neuronTrafo(X, W(count), b(count))
        val inputZactive = NeuralNetwork.activate(inputZ, activation)
        applyLayer(inputZactive, count+1)
      }
      else {
        NeuralNetwork.neuronTrafo(X, W(count), b(count))
      }
    applyLayer(X, 0)
  }

  def getProbabilities(X: DenseMatrix[Double], W: IndexedSeq[DenseMatrix[Double]], b: IndexedSeq[DenseVector[Double]], activation: String): DenseMatrix[Double] = {
    val Z = NeuralNetwork.feedForward(X, W, b, activation)
    val expScores = exp(Z)
    val expSums = sum(expScores(*, ::))
    expScores(::, *) / expSums  // softmax
  }

  def getLoss(X: DenseMatrix[Double], y: DenseVector[Int], W: IndexedSeq[DenseMatrix[Double]], b: IndexedSeq[DenseVector[Double]], activation: String, regularization: Double): Double = {
    val probs: DenseMatrix[Double] = NeuralNetwork.getProbabilities(X, W, b, activation)
    val correctLogProbs: DenseVector[Double] = DenseVector.tabulate(y.size){i => -Math.log(probs(i, y(i)))}
    val dataLoss: Double = correctLogProbs.sum
    val dataLossReg: Double = dataLoss + regularization / 2 * W.map(w => pow(w, 2).sum).sum
    dataLossReg / X.rows
  }

}
