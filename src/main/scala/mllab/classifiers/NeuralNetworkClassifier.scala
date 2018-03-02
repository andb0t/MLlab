package classifiers

import breeze.linalg._
import breeze.numerics._

import utils._


class NeuralNetworkClassifier(
  alpha: Double = 0.01,
  alphaHalflife: Int = 100,
  alphaDecay: String = "exp",
  regularization: Double = 0.01,
  activation: String = "tanh",
  batchSize: Int = -1
) extends Classifier {

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
    else if (activation == "logistic") 1.0 / (exp(-Z) + 1.0)
    else if (activation == "identity") 0.01 * Z  // TODO: fix it: NaN in training if not scaled down
    else if (activation == "RELU") I(Z :> 0.0) *:* Z
    else if (activation == "leakyRELU") (I(Z :> 0.0) + 0.1 * I(Z :<= 0.0)) *:* Z
    else throw new Exception("activation function not implented")

  def derivActivate(A: DenseMatrix[Double]): DenseMatrix[Double] =
    if (activation == "tanh") 1.0 - pow(A, 2)
    else if (activation == "logistic") A *:* (1.0 - A)
    else if (activation == "identity") DenseMatrix.ones[Double](A.rows, A.cols)
    else if (activation == "RELU") I(A :> 0.0)
    else if (activation == "leakyRELU") I(A :> 0.0) + 0.1 * I(A :<= 0.0)
    else throw new Exception("activation function not implented")

  def probabilities(Z: DenseMatrix[Double]): DenseMatrix[Double] = {
    val expScores = exp(Z)
    val expSums = sum(expScores(*, ::))
    expScores(::, *) / expSums
  }

  def getProbabilities(X: DenseMatrix[Double]): DenseMatrix[Double] = {
    def applyLayer(X: DenseMatrix[Double], count: Int): DenseMatrix[Double] =
      if (count < layers.length - 2) {
        val inputZ = neuronTrafo(X, W(count), b(count))
        val inputZactive = activate(inputZ)
        applyLayer(inputZactive, count+1)
      }
      else {
        neuronTrafo(X, W(count), b(count))
      }
    val middleZ = applyLayer(X, 0)
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
      val decayedAlpha: Double =
        if (alphaDecay == "step") alpha / Math.pow(2, Math.floor(count.toFloat / alphaHalflife))
        else if (alphaDecay == "exp") alpha * Math.exp(-1.0 * count / alphaHalflife)
        else alpha

      if (count < maxEpoch) {
        if (count % 100 == 0) println(s"- epoch $count: alpha %.2e, loss %.4e".format(decayedAlpha, getLoss(X, y)))

        val thisBatch: Seq[Int] =
          if (batchSize != -1)  Seq.fill(batchSize)(scala.util.Random.nextInt(X.rows))
          else 0 until X.rows
        val thisX: DenseMatrix[Double] = X(thisBatch, ::).toDenseMatrix
        val thisy: DenseVector[Int] = y(thisBatch).toDenseVector

        // forward propagation
        val activationLayer: DenseMatrix[Double] = activate(neuronTrafo(thisX, W(0), b(0)))  // (nInstances, 10)

        // backward propagation layer 1
        val probs: DenseMatrix[Double] = getProbabilities(thisX)  // (nInstances, 2)
        val outputDelta: DenseMatrix[Double] = DenseMatrix.tabulate(thisX.rows, inputLayer){
          case (i, j) => if (j == thisy(i)) probs(i, j) - 1 else probs(i, j)
        }
        val dW1: DenseMatrix[Double] = activationLayer.t * outputDelta + regularization *:* W(1)  // (10, 2)
        val db1: DenseVector[Double] = sum(outputDelta.t(*, ::))  // (2)

        // backward propagation layer 0
        val partDerivWeight: DenseMatrix[Double] = outputDelta * W(1).t  // (nInstances, 10)
        val partDerivActiv: DenseMatrix[Double] = derivActivate(activationLayer)  // (nInstances, 10)
        val middleDelta: DenseMatrix[Double] = partDerivWeight *:* partDerivActiv  // (nInstances, 10)
        val dW0: DenseMatrix[Double] = thisX.t * middleDelta + regularization *:* W(0)  // (2, 10)
        val db0: DenseVector[Double] = sum(middleDelta.t(*, ::))  // (10)

        // updates
        W(1) :+= -decayedAlpha *:* dW1
        b(1) :+= -decayedAlpha *:* db1
        W(0) :+= -decayedAlpha *:* dW0
        b(0) :+= -decayedAlpha *:* db0

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
