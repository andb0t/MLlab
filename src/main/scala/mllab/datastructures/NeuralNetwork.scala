package datastructures

import breeze.linalg._
import breeze.numerics._


/** Provides functions for neural network training */
object NeuralNetwork {

  /** Performs neuron transformation in one layer from n inputs to m outputs
   * @param X Instance vector with n features
   * @param W Matrix of dimension n x m, n weights for m neurons
   * @param b Vector with m entries, one intercept for each neuron
   * @return Instance vector with m features
   */
  def neuronTrafo(X: DenseMatrix[Double], W: DenseMatrix[Double], b: DenseVector[Double]): DenseMatrix[Double] =
    X * W + DenseVector.ones[Double](X.rows) * b.t

  /** Performs the activation of a layer output
   * @param Z Instance vectors with neuron-transformed features
   * @param activation Activation function identifier
   * @return Activated layer output vector with same dimensions as Z
   */
  def activate(Z: DenseMatrix[Double], activation: String): DenseMatrix[Double] =
    if (activation == "tanh") tanh(Z)
    else if (activation == "logistic") 1.0 / (exp(-Z) + 1.0)
    else if (activation == "identity") 0.01 * Z  // TODO: fix it: NaN in training if not scaled down
    else if (activation == "RELU") I(Z :> 0.0) *:* Z
    else if (activation == "leakyRELU") (I(Z :> 0.0) + 0.1 * I(Z :<= 0.0)) *:* Z
    else if (activation == "perceptron") I(Z :> 0.0)
    else throw new Exception("activation function not implented")

  /** Calculates the derivative of the activation layer for their output for in backpropagation.
   * @param A Instance vectors with with activated features
   * @param activation Activation function identifier
   * @return Activated layer output vector with same dimensions as Z
   */
  def derivActivate(A: DenseMatrix[Double], activation: String): DenseMatrix[Double] =
    if (activation == "tanh") 1.0 - pow(A, 2)
    else if (activation == "logistic") A *:* (1.0 - A)
    else if (activation == "identity") DenseMatrix.ones[Double](A.rows, A.cols)
    else if (activation == "RELU") I(A :> 0.0)
    else if (activation == "leakyRELU") I(A :> 0.0) + 0.1 * I(A :<= 0.0)
    else if (activation == "perceptron") DenseMatrix.zeros[Double](A.rows, A.cols)
    else throw new Exception("activation function not implented")

  /** Creates the neural network output by feeding all instances the network
   * @param X List of input instance feature vectors
   * @param W Sequence of weight matrices of the layers
   * @param b Sequence of intercept vectors of the layers
   * @param activation Activation function identifier
   * @return Output neuron values for each instance
   */
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

  /** Calculates for each instance the probability vector to belong to each class from the relative output neuron values
   * @param X List of instance feature vectors
   * @param W Sequence of weight matrices of the layers
   * @param b Sequence of intercept vectors of the layers
   * @param activation Activation function identifier
   * @return Vectors of probabilities to belong th each class
   */
  def getProbabilities(X: DenseMatrix[Double], W: IndexedSeq[DenseMatrix[Double]], b: IndexedSeq[DenseVector[Double]], activation: String): DenseMatrix[Double] = {
    val Z = NeuralNetwork.feedForward(X, W, b, activation)
    val expScores = exp(Z)
    val expSums = sum(expScores(*, ::))
    expScores(::, *) / expSums  // softmax
  }

  /** Calculates the classification loss of the predictions vs the truth
   * @param X List of instance feature vectors
   * @param y List of instance labels
   * @param W Sequence of weight matrices of the layers
   * @param b Sequence of intercept vectors of the layers
   * @param activation Activation function identifier
   * @param regularization Regularization parameter
   */
  def getLoss(X: DenseMatrix[Double], y: DenseVector[Int], W: IndexedSeq[DenseMatrix[Double]], b: IndexedSeq[DenseVector[Double]], activation: String, regularization: Double): Double = {
    val probs: DenseMatrix[Double] = NeuralNetwork.getProbabilities(X, W, b, activation)
    val correctLogProbs: DenseVector[Double] = DenseVector.tabulate(y.size){i => -Math.log(probs(i, y(i)))}
    val dataLoss: Double = correctLogProbs.sum
    val dataLossReg: Double = dataLoss + regularization / 2 * W.map(w => pow(w, 2).sum).sum
    dataLossReg / X.rows
  }

  /** Propagates the instances forward and saves the intermediate output
   * @param X List of instance feature vectors
   * @param W Sequence of weight matrices of the layers
   * @param b Sequence of intercept vectors of the layers
   * @param activation Activation function identifier
   * @return List of layer output vectors for all instances
   */
  def propagateForward(
    X: DenseMatrix[Double],
    W: IndexedSeq[DenseMatrix[Double]],
    b: IndexedSeq[DenseVector[Double]],
    activation: String
  ): List[DenseMatrix[Double]] = {
    def walkLayersForward(inputA: DenseMatrix[Double], count: Int, upd: List[DenseMatrix[Double]]): List[DenseMatrix[Double]] = {
      if (count < b.size - 1) {
        val Z: DenseMatrix[Double] = NeuralNetwork.neuronTrafo(inputA, W(count), b(count))  // (nInstances, 10)
        val A: DenseMatrix[Double] = NeuralNetwork.activate(Z, activation)  // (nInstances, 10)
        walkLayersForward(A, count + 1, A :: upd)
      }
      else upd
    }
    walkLayersForward(X, 0, Nil).reverse
  }

  /** Propagates the network outputs backward and saves necessary updates for weights and intercepts
  * @param delta List of output distances from truth for each output neuron for each instance
  * @param A List of layer output vectors for all instances from forward propagation
  * @param X List of input instance feature vectors
  * @param W Sequence of weight matrices of the layers
  * @param b Sequence of intercept vectors of the layers
  * @param activation Activation function identifier
  * @param regularization Regularization parameter
  * @return List of updates for weight matrix and intercept vector
  */
  def propagateBack(
    delta: DenseMatrix[Double],
    A: List[DenseMatrix[Double]],
    X: DenseMatrix[Double],
    W: IndexedSeq[DenseMatrix[Double]],
    b: IndexedSeq[DenseVector[Double]],
    activation: String,
    regularization: Double
  ): List[Tuple2[DenseMatrix[Double], DenseVector[Double]]] = {
    def walkLayersBack(deltaPlus: DenseMatrix[Double], count: Int, upd: List[Tuple2[DenseMatrix[Double], DenseVector[Double]]]): List[Tuple2[DenseMatrix[Double], DenseVector[Double]]] = {
      if (count >= 0) {
        val partDerivCost: DenseMatrix[Double] = deltaPlus * W(count+1).t  // (nInstances, 10)
        val partDerivActiv: DenseMatrix[Double] = NeuralNetwork.derivActivate(A(count), activation)  // (nInstances, 10)
        val instanceDelta: DenseMatrix[Double] = partDerivCost *:* partDerivActiv  // (nInstances, 10)
        val db: DenseVector[Double] = sum(instanceDelta.t(*, ::))  // (10)
        val inputA = if (count > 0) A(count - 1) else X
        val dW: DenseMatrix[Double] = inputA.t * instanceDelta + regularization *:* W(count)  // (2, 10)
        walkLayersBack(instanceDelta, count - 1, (dW, db)::upd)
      }
      else upd
    }
    walkLayersBack(delta, b.size - 2, Nil)
  }

}
