package classifiers

import breeze.linalg._
import breeze.numerics._

import utils._


class NeuralNetworkClassifier(alpha: Double = 0.01, regularization: Double = 0.01) extends Classifier {

  val inputLayer: Int = 2
  val middleLayer: Int = 4
  val outputLayer: Int = 2  // == 2 required for this implementation of binary classification
  val layers: List[Int] = List(inputLayer, middleLayer, outputLayer)

  val W = for (i <- 0 until layers.length - 1) yield DenseMatrix.rand[Double](layers(i),layers(i+1))
  val b = for (i <- 0 until layers.length - 1) yield DenseVector.zeros[Double](layers(i+1))


  def neuronTrafo(X: DenseMatrix[Double], W: DenseMatrix[Double], b: DenseVector[Double]): DenseMatrix[Double] =
    X * W + DenseVector.ones[Double](X.rows) * b.t

  def activate(Z: DenseMatrix[Double]): DenseMatrix[Double] = tanh(Z)

  def probabilities(Z: DenseMatrix[Double]): DenseMatrix[Double] = {
    val expScores = exp(Z)
    val expSums = sum(expScores(*, ::))
    expScores(::, *) / expSums
  }

  def getProbabilities(X: DenseMatrix[Double]): DenseMatrix[Double] = {
    val inputZ = neuronTrafo(X, W(0), b(0))
    val inputZactive = activate(inputZ)
    val middleZ = neuronTrafo(inputZactive, W(1), b(1))
    val probs = probabilities(middleZ)
    probs
  }

  def getLoss(X: DenseMatrix[Double], y: DenseVector[Int]): Double = {
    val probs: DenseMatrix[Double] = getProbabilities(X)
    val correctLogProbs: DenseVector[Double] = DenseVector.tabulate(y.size){i => -Math.log(probs(i, y(i)))}
    val dataLoss: Double = correctLogProbs.sum
    val dataLossReg: Double = dataLoss + regularization / 2 * W.map(w => pow(w, 2).sum).sum
    val loss: Double = dataLossReg / X.rows
    loss
  }

  def train(Xraw: List[List[Double]], yraw: List[Int]): Unit = {
    require(Xraw.length == yraw.length, "both arguments must have the same length")

    val X: DenseMatrix[Double] = DenseMatrix(Xraw.flatten).reshape(inputLayer, Xraw.length).t
    val y: DenseVector[Int] = DenseVector(yraw.toArray)

    println("Apply backpropagation gradient descent")
    val maxEpoch: Int = 1000

    def gradientDescent(count: Int): Unit = {
      // a simple implementation: http://www.wildml.com/2015/09/implementing-a-neural-network-from-scratch/
      // check http://neuralnetworksanddeeplearning.com/chap2.html
      if (count < maxEpoch) {
        if (count % 100 == 0) println(s"- epoch $count: loss " + getLoss(X, y))

        // forward propagation
        val activationLayer: DenseMatrix[Double] = activate(neuronTrafo(X, W(0), b(0)))  // (nInstances, 10)
        // println("Instances: " + activationLayer.length + ",  new features: " + activationLayer.head.length)
        val probs: DenseMatrix[Double] = getProbabilities(X)  // (nInstances, 2)
        // backward propagation
        val outputDelta: DenseMatrix[Double] = probs *:* 1.0  // (nInstances, 2)

        for (i <- 0 until y.size) outputDelta(i, y(i)) -= 1  // this is imperative programming :(
        // DenseVector.tabulate(y.size){i => -Math.log(probs(i, y(i)))}  // maybe use construction like this here?

        val dW1: DenseMatrix[Double] = activationLayer.t * outputDelta  // (10, 2)
        val db1: DenseVector[Double] = sum(outputDelta.t(*, ::))  // (2)
        // delta2 = outputDelta.dot(W(1).T) * (1 - np.power(activationLayer, 2))
        val partialDerivWeight: DenseMatrix[Double] = outputDelta * W(1).t  // (nInstances, 10)
        // val particalDerivActiv = activationLayer.map(al => al.map(ae => 1 - Math.pow(ae, 2)))  // (nInstances, 10)
        val particalDerivActiv: DenseMatrix[Double] = 1.0 - pow(activationLayer, 2)  // (nInstances, 10)
        val middleDelta: DenseMatrix[Double] = partialDerivWeight *:* particalDerivActiv  // (nInstances, 10)
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


  def predict(Xraw: List[List[Double]]): List[Int] = {

    val X: DenseMatrix[Double] = DenseMatrix(Xraw.flatten).reshape(inputLayer, Xraw.length).t

    val probs = getProbabilities(X)
    val prediction: DenseVector[Int] = argmax(probs(*, ::))
    (for (i <- 0 until prediction.size) yield prediction(i)).toList
  }

}
