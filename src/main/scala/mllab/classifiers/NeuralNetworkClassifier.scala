package classifiers

import breeze.linalg._
import breeze.numerics._

import utils._


class NeuralNetworkClassifier(alpha: Double = 0.01, regularization: Double = 0.01) extends Classifier {

  val inputLayer: Int = 2
  val middleLayer: Int = 10
  val outputLayer: Int = 2  // == 2 required for this implementation of binary classification
  val layers: List[Int] = List(inputLayer, middleLayer, outputLayer)

  val W = for (i <- 0 until layers.length - 1) yield DenseMatrix.rand[Double](layers(i),layers(i+1))
  val b = for (i <- 0 until layers.length - 1) yield DenseVector.zeros[Double](layers(i+1))


  def neuronTrafo(X: List[DenseVector[Double]], W: DenseMatrix[Double], b: DenseVector[Double]): List[DenseVector[Double]] =
    for (x <- X) yield W.t * x + b

  def activate(Z: List[DenseVector[Double]]): List[DenseVector[Double]] =
    Z.map(tanh(_))

  def probabilities(Z: List[DenseVector[Double]]): List[DenseVector[Double]] = {
    val expScores = Z.map(z => exp(z))
    expScores.map(s => s / sum(s))
  }

  def getProbabilities(X: List[DenseVector[Double]]): List[DenseVector[Double]] = {

    val inputZ = neuronTrafo(X, W(0), b(0))
    // // println(inputZ)
    val inputZactive = activate(inputZ)
    // // println(inputZactive)
    val middleZ = neuronTrafo(inputZactive, W(1), b(1))
    // // println(middleZ)
    val probs = probabilities(middleZ)
    // // println(probs)
    probs
  }

  def getLoss(X: List[DenseVector[Double]], y: List[Int]): Double = {
    val probs = getProbabilities(X)
    // val correctLogProbs: List[Vector] = (probs zip y).
    //   map{case (v, i) => for (vs <- v if (vs == v(i))) yield  - Math.log(vs)}
    // val dataLoss = correctLogProbs.flatten.sum
    // val dataLossReg = dataLoss + regularization / 2 * W.map(w => Maths.squareM(w).flatten.sum).sum
    // val loss: Double = dataLossReg / X.length
    // loss

    0
  }

  def train(Xraw: List[List[Double]], y: List[Int]): Unit = {
    require(Xraw.length == y.length, "both arguments must have the same length")

    val X: List[DenseVector[Double]] = for (x <- Xraw) yield DenseVector(x.toArray)

    println("Apply backpropagation gradient descent")
    val maxEpoch: Int = 1000

    def gradientDescent(count: Int): Unit = {
      // a simple implementation: http://www.wildml.com/2015/09/implementing-a-neural-network-from-scratch/
      // check http://neuralnetworksanddeeplearning.com/chap2.html
      if (count < maxEpoch) {

        if (count % 100 == 0) println(s"- epoch $count: loss " + getLoss(X, y))

        // forward propagation
        val activationLayer = activate(neuronTrafo(X, W(0), b(0)))  // (nInstances, 10)
        // println("Instances: " + activationLayer.length + ",  new features: " + activationLayer.head.length)
        val probs = getProbabilities(X)
        // // backward propagation
        // val outputDelta: List[Vector] = (probs zip y).
        //   map{case (v, i) => for (vs <- v) yield if (vs == v(i)) vs - 1 else vs}  // (nInstances, 2)
        // val dW1: Matrix = Maths.timesMM(activationLayer.transpose, outputDelta)  // (10, 2)
        // val db1: Vector = outputDelta.transpose.map(_.sum)  // (2)
        // // delta2 = outputDelta.dot(W(1).T) * (1 - np.power(activationLayer, 2))
        // val partialDerivWeight = Maths.timesMM(outputDelta, W(1).transpose)  // (nInstances, 10)
        // val particalDerivActiv = activationLayer.map(al => al.map(ae => 1 - Math.pow(ae, 2)))  // (nInstances, 10)
        // val middleDelta: List[Vector] = Maths.hadamardMM(partialDerivWeight, particalDerivActiv)  // (nInstances, 10)
        // val dW0: Matrix = Maths.timesMM(X.transpose, middleDelta)  // (2, 10)
        // val db0: Vector = middleDelta.transpose.map(_.sum)  // (10)
        // // regularization
        // val dW1reg: Matrix = Maths.plusM(dW1, Maths.timesM(regularization, W(1)))
        // val dW0reg: Matrix = Maths.plusM(dW0, Maths.timesM(regularization, W(0)))
        // // updates
        // val newW1: Matrix = Maths.plusM(W(1), Maths.timesM(-alpha, dW1reg))
        // val newW0: Matrix = Maths.plusM(W(0), Maths.timesM(-alpha, dW0reg))
        // val newb1: Vector = Maths.plus(b(1), Maths.times(-alpha, db1))
        // val newb0: Vector = Maths.plus(b(0), Maths.times(-alpha, db0))
        // // updates weights
        // W.clear()
        // b.clear()
        // List(newW0, newW1).copyToBuffer(W)
        // List(newb0, newb1).copyToBuffer(b)

        gradientDescent(count + 1)
      }else println(s"Training finished after $count epochs with loss " + getLoss(X, y))
    }

    gradientDescent(0)

  }


  def predict(Xraw: List[List[Double]]): List[Int] = {

    val X: List[DenseVector[Double]] = for (x <- Xraw) yield DenseVector(x.toArray)

    val probs = getProbabilities(X)
    val prediction = probs.map(argmax(_))
    prediction
  }

}
