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
  batchSize: Int = -1,
  layers: List[Int] = List(2, 4, 2)
  //loss: String = "cross-entropy / quadratic / log cross-entropy"
) extends Classifier {

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
    else if (activation == "perceptron") I(Z :> 0.0)
    else throw new Exception("activation function not implented")

  def derivActivate(A: DenseMatrix[Double]): DenseMatrix[Double] =
    if (activation == "tanh") 1.0 - pow(A, 2)
    else if (activation == "logistic") A *:* (1.0 - A)
    else if (activation == "identity") DenseMatrix.ones[Double](A.rows, A.cols)
    else if (activation == "RELU") I(A :> 0.0)
    else if (activation == "leakyRELU") I(A :> 0.0) + 0.1 * I(A :<= 0.0)
    else if (activation == "perceptron") DenseMatrix.zeros[Double](A.rows, A.cols)
    else throw new Exception("activation function not implented")

  def feedForward(X: DenseMatrix[Double]): DenseMatrix[Double] = {
    def applyLayer(X: DenseMatrix[Double], count: Int): DenseMatrix[Double] =
      if (count < layers.length - 2) {
        val inputZ = neuronTrafo(X, W(count), b(count))
        val inputZactive = activate(inputZ)
        applyLayer(inputZactive, count+1)
      }
      else {
        neuronTrafo(X, W(count), b(count))
      }
      applyLayer(X, 0)
    }

  def getProbabilities(X: DenseMatrix[Double]): DenseMatrix[Double] = {
    val Z = feedForward(X)
    val expScores = exp(Z)
    val expSums = sum(expScores(*, ::))
    expScores(::, *) / expSums  // softmax
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
        if (count % 100 == 0 || (count < 50 && count % 10 == 0) || (count < 5))
          println(s"- epoch $count: alpha %.2e, loss %.4e".format(decayedAlpha, getLoss(X, y)))

        val thisBatch: Seq[Int] =
          if (batchSize != -1)  Seq.fill(batchSize)(scala.util.Random.nextInt(X.rows))
          else 0 until X.rows
        val thisX: DenseMatrix[Double] = X(thisBatch, ::).toDenseMatrix
        val thisy: DenseVector[Int] = y(thisBatch).toDenseVector

        // forward propagation
        def propagateForward(
          inputA: DenseMatrix[Double],
          count: Int,
          upd: List[DenseMatrix[Double]]
        ): List[DenseMatrix[Double]] = {
          if (count < layers.length - 2) {
            val Z: DenseMatrix[Double] = neuronTrafo(inputA, W(count), b(count))  // (nInstances, 10)
            val A: DenseMatrix[Double] = activate(Z)  // (nInstances, 10)
            propagateForward(A, count + 1, A :: upd)
          }
          else upd
        }
        val A = propagateForward(thisX, 0, Nil).reverse

        // println("A " + A.length)
        // for (a <- A) println("A (" + a.rows + "," + a.cols + ")")
        // println("A0 (" + A0.rows + "," + A0.cols + ")")

        // backward propagation

        // output layer
        val probs: DenseMatrix[Double] = getProbabilities(thisX)  // (nInstances, 2)
        val deltaOutput: DenseMatrix[Double] = DenseMatrix.tabulate(thisX.rows, layers.head){
          case (i, j) => if (j == thisy(i)) probs(i, j) - 1 else probs(i, j)
        }
        val dWoutput: DenseMatrix[Double] = A(layers.length - 3).t * deltaOutput + regularization *:* W(layers.length - 2)  // (10, 2)
        val dboutput: DenseVector[Double] = sum(deltaOutput.t(*, ::))  // (2)

        // other layers
        def propagateBack(
          deltaPlus: DenseMatrix[Double],
          count: Int,
          upd: List[Tuple2[DenseMatrix[Double], DenseVector[Double]]]
        ): List[Tuple2[DenseMatrix[Double], DenseVector[Double]]] = {
          if (count >= 0) {
            val partDerivCost: DenseMatrix[Double] = deltaPlus * W(count+1).t  // (nInstances, 10)
            val partDerivActiv: DenseMatrix[Double] = derivActivate(A(count))  // (nInstances, 10)
            val delta: DenseMatrix[Double] = partDerivCost *:* partDerivActiv  // (nInstances, 10)
            val db: DenseVector[Double] = sum(delta.t(*, ::))  // (10)
            val inputA = if (count > 0) A(count - 1) else thisX
            val dW: DenseMatrix[Double] = inputA.t * delta + regularization *:* W(count)  // (2, 10)
            propagateBack(delta, count - 1, (dW, db)::upd)
          }
          else upd
        }
        val dWdb = propagateBack(deltaOutput, layers.length - 3, Nil)
        // println("dWdb " + dWdb.length)
        // for (upd <- dWdb) println("dW (" + upd._1.rows + "," + upd._1.cols + ") db (" + upd._2.size + ")")

        def updateWeights(count: Int): Unit = {
          if (count < layers.length - 2) {
            W(count) :+= -decayedAlpha *:* dWdb(count)._1
            b(count) :+= -decayedAlpha *:* dWdb(count)._2
            updateWeights(count + 1)
          }
          else{
            W(layers.length - 2) :+= -decayedAlpha *:* dWoutput
            b(layers.length - 2) :+= -decayedAlpha *:* dboutput
          }
        }
        updateWeights(0)


        gradientDescent(count + 1)
      }else println(s"Training finished after $count epochs with loss " + getLoss(X, y))
    }

    gradientDescent(0)

  }


  def predict(listX: List[List[Double]]): List[Int] = {
    val X: DenseMatrix[Double] = Trafo.toMatrix(listX)
    val output = feedForward(X)
    val prediction: DenseVector[Int] = argmax(output(*, ::))
    (for (i <- 0 until prediction.size) yield prediction(i)).toList
  }

}
