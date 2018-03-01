package classifiers

import breeze.linalg._
import breeze.numerics._

import utils._


class NeuralNetworkClassifier(alpha: Double = 0.01, regularization: Double = 0.01, activation: String = "tanh") extends Classifier {

  val inputLayer: Int = 2
  val middleLayer: Int = 4
  val outputLayer: Int = 2  // == 2 required for this implementation of binary classification
  val layers: List[Int] = List(inputLayer, middleLayer, outputLayer)

  val W = for (i <- 0 until layers.length - 1) yield DenseMatrix.rand[Double](layers(i),layers(i+1))
  val b = for (i <- 0 until layers.length - 1) yield DenseVector.zeros[Double](layers(i+1))


  def neuronTrafo(X: DenseMatrix[Double], W: DenseMatrix[Double], b: DenseVector[Double]): DenseMatrix[Double] =
    X * W + DenseVector.ones[Double](X.rows) * b.t

  def activate(Z: DenseMatrix[Double]): DenseMatrix[Double] =
    if (activation == "tanh") tanh(Z)
    else if (activation == "logistic") 1.0 / (exp(Z) + 1.0)
    else if (activation == "identity") 0.01 * Z  // TODO: fix it: NaN in training if not scaled down
    else throw new Exception("activation function not implented")

  def derivActivate(A: DenseMatrix[Double]): DenseMatrix[Double] =
    if (activation == "tanh") 1.0 - pow(A, 2)
    else if (activation == "logistic") A *:* (1.0 - A)
    else if (activation == "identity") DenseMatrix.ones[Double](A.rows, A.cols)
    else throw new Exception("activation function not implented")

  def probabilities(Z: DenseMatrix[Double]): DenseMatrix[Double] = {
    val expScores = exp(Z)
    val expSums = sum(expScores(*, ::))
    expScores(::, *) / expSums
  }

  def getProbabilities(X: DenseMatrix[Double]): DenseMatrix[Double] = {
    val inputZ = neuronTrafo(X, W(0), b(0))
    val inputZactive = activate(inputZ)
    val middleZ = neuronTrafo(inputZactive, W(1), b(1))
    probabilities(middleZ)
  }

  def getLoss(X: DenseMatrix[Double], y: DenseVector[Int]): Double = {
    val probs: DenseMatrix[Double] = getProbabilities(X)
    val correctLogProbs: DenseVector[Double] = DenseVector.tabulate(y.size){i => -Math.log(probs(i, y(i)))}
    val dataLoss: Double = correctLogProbs.sum
    val dataLossReg: Double = dataLoss + regularization / 2 * W.map(w => pow(w, 2).sum).sum
    dataLossReg / X.rows
  }

  def train(listX: List[List[Double]], listy: List[Int]): Unit = {
    require(listX.length == listy.length, "number of training instances and labels is not equal")
    val X: DenseMatrix[Double] = Trafo.toMatrix(listX)
    val y: DenseVector[Int] = Trafo.toVectorInt(listy)

    println("Apply backpropagation gradient descent")
    val maxEpoch: Int = 1000

    def gradientDescent(count: Int): Unit = {
      // a simple implementation: http://www.wildml.com/2015/09/implementing-a-neural-network-from-scratch/
      // check http://neuralnetworksanddeeplearning.com/chap2.html
      if (count < maxEpoch) {
        if (count % 100 == 0) println(s"- epoch $count: loss " + getLoss(X, y))

        // forward propagation
        val activationLayer: DenseMatrix[Double] = activate(neuronTrafo(X, W(0), b(0)))  // (nInstances, 10)
        val probs: DenseMatrix[Double] = getProbabilities(X)  // (nInstances, 2)
        // backward propagation
        val outputDelta: DenseMatrix[Double] = DenseMatrix.tabulate(X.rows, inputLayer){
          case (i, j) => if (j == y(i)) probs(i, j) - 1 else probs(i, j)
        }
        val dW1: DenseMatrix[Double] = activationLayer.t * outputDelta  // (10, 2)
        val db1: DenseVector[Double] = sum(outputDelta.t(*, ::))  // (2)
        val partDerivWeight: DenseMatrix[Double] = outputDelta * W(1).t  // (nInstances, 10)
        val partDerivActiv: DenseMatrix[Double] = derivActivate(activationLayer)  // (nInstances, 10)
        val middleDelta: DenseMatrix[Double] = partDerivWeight *:* partDerivActiv  // (nInstances, 10)
        val dW0: DenseMatrix[Double] = X.t * middleDelta  // (2, 10)
        val db0: DenseVector[Double] = sum(middleDelta.t(*, ::))  // (10)
        // // regularization
        val dW1reg: DenseMatrix[Double] = dW1 + regularization *:* W(1)
        val dW0reg: DenseMatrix[Double] = dW0 + regularization *:* W(0)
        // // updates
        W(1) :+= -alpha *:* dW1reg
        W(0) :+= -alpha *:* dW0reg
        b(1) :+= -alpha *:* db1
        b(0) :+= -alpha *:* db0

        gradientDescent(count + 1)
      }else println(s"Training finished after $count epochs with loss " + getLoss(X, y))
    }

    gradientDescent(0)

  }


  def predict(listX: List[List[Double]]): List[Int] = {
    val X: DenseMatrix[Double] = Trafo.toMatrix(listX)
    val probs = getProbabilities(X)
    val prediction: DenseVector[Int] = argmax(probs(*, ::))
    (for (i <- 0 until prediction.size) yield prediction(i)).toList
  }

}
