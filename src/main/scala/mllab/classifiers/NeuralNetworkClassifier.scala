package classifiers

import scala.collection.mutable.ListBuffer

import breeze.linalg._
import breeze.numerics._

import utils._
import datastructures._


/** Neural network classifier
 * @param alpha Learning rate
 * @param alphaHalflife Learning rate decay after this number of training steps
 * @param alphaDecay Type of learning rate decay
 * @param regularization Regularization parameter
 * @param activation Activation function
 * @param batchSize Number of (randomized) training instances to use for each training step
 * @param layers Structure of the network as a list of number of neurons in each layer
 */
class NeuralNetworkClassifier(
  alpha: Double = 0.01,
  alphaHalflife: Int = 100,
  alphaDecay: String = "exp",
  regularization: Double = 0.01,
  activation: String = "tanh",
  batchSize: Int = -1,
  layers: List[Int] = List(2, 4, 2)
  // seed: Int = 1337,
  //loss: String = "cross-entropy / quadratic / log cross-entropy"
) extends Classifier {

  require(layers.length > 2, "too few layers: need at least an input, a middle and an output layer")

  val name: String = "NeuralNetworkClassifier"

  val W: IndexedSeq[DenseMatrix[Double]] = for (i <- 0 until layers.length - 1) yield DenseMatrix.rand[Double](layers(i),layers(i+1))
  val b: IndexedSeq[DenseVector[Double]] = for (i <- 0 until layers.length - 1) yield DenseVector.zeros[Double](layers(i+1))

  var lossEvolution = new ListBuffer[(Double, Double)]()
  var alphaEvolution = new ListBuffer[(Double, Double)]()

  /** Propagates the instances forward and saves the intermediate output
   * @param X List of instance feature vectors
   * @param W Sequence of weight matrices of the layers
   * @param b Sequence of intercept vectors of the layers
   * @param activation Activation function identifier
   */
  def propagateForward(X: DenseMatrix[Double], W: IndexedSeq[DenseMatrix[Double]], b: IndexedSeq[DenseVector[Double]], activation: String): List[DenseMatrix[Double]] = {
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

  def train(listX: List[List[Double]], listy: List[Int]): Unit = {
    require(listX.length == listy.length, "number of training instances and labels is not equal")
    val X: DenseMatrix[Double] = Trafo.toMatrix(listX)
    val y: DenseVector[Int] = Trafo.toVectorInt(listy)

    println("Apply backpropagation gradient descent")
    val maxEpoch: Int = 1000

    def gradientDescent(count: Int): Unit = {
      // a simple implementation: http://www.wildml.com/2015/09/implementing-a-neural-network-from-scratch/
      // more profound explanation: http://neuralnetworksanddeeplearning.com/chap2.html
      val decayedAlpha: Double =
        if (alphaDecay == "step") alpha / Math.pow(2, Math.floor(count.toFloat / alphaHalflife))
        else if (alphaDecay == "exp") alpha * Math.exp(-1.0 * count / alphaHalflife)
        else alpha

      alphaEvolution += Tuple2(count.toDouble, decayedAlpha)

      if (count < maxEpoch) {
        if (count % 100 == 0 || (count < 50 && count % 10 == 0) || (count < 5)) {
          val loss = NeuralNetwork.getLoss(X, y, W, b, activation, regularization)
          lossEvolution += Tuple2(count.toDouble, loss)
          println("- epoch% 4d: alpha %.2e, loss %.4e".format(count, decayedAlpha, loss))
        }

        val thisBatch: Seq[Int] =
          if (batchSize != -1)  Seq.fill(batchSize)(scala.util.Random.nextInt(X.rows))
          else 0 until X.rows
        val thisX: DenseMatrix[Double] = X(thisBatch, ::).toDenseMatrix
        val thisy: DenseVector[Int] = y(thisBatch).toDenseVector

        // forward propagation
        val A = propagateForward(thisX, W, b, activation)

        // backward propagation

        // output layer
        val probs: DenseMatrix[Double] = NeuralNetwork.getProbabilities(thisX, W, b, activation)  // (nInstances, 2)
        val deltaOutput: DenseMatrix[Double] = DenseMatrix.tabulate(thisX.rows, layers.head){
          case (i, j) => if (j == thisy(i)) probs(i, j) - 1 else probs(i, j)
        }
        val dWoutput: DenseMatrix[Double] = A(b.size - 2).t * deltaOutput + regularization *:* W(b.size - 1)  // (10, 2)
        val dboutput: DenseVector[Double] = sum(deltaOutput.t(*, ::))  // (2)

        // other layers
        def propagateBack(deltaPlus: DenseMatrix[Double], count: Int, upd: List[Tuple2[DenseMatrix[Double], DenseVector[Double]]]): List[Tuple2[DenseMatrix[Double], DenseVector[Double]]] = {
          if (count >= 0) {
            val partDerivCost: DenseMatrix[Double] = deltaPlus * W(count+1).t  // (nInstances, 10)
            val partDerivActiv: DenseMatrix[Double] = NeuralNetwork.derivActivate(A(count), activation)  // (nInstances, 10)
            val delta: DenseMatrix[Double] = partDerivCost *:* partDerivActiv  // (nInstances, 10)
            val db: DenseVector[Double] = sum(delta.t(*, ::))  // (10)
            val inputA = if (count > 0) A(count - 1) else thisX
            val dW: DenseMatrix[Double] = inputA.t * delta + regularization *:* W(count)  // (2, 10)
            propagateBack(delta, count - 1, (dW, db)::upd)
          }
          else upd
        }
        val dWdb = propagateBack(deltaOutput, b.size - 2, Nil)

        def updateWeights(count: Int): Unit = {
          if (count < b.size - 1) {
            W(count) :+= -decayedAlpha *:* dWdb(count)._1
            b(count) :+= -decayedAlpha *:* dWdb(count)._2
            updateWeights(count + 1)
          }
          else{
            W(b.size - 1) :+= -decayedAlpha *:* dWoutput
            b(b.size - 1) :+= -decayedAlpha *:* dboutput
          }
        }
        updateWeights(0)


        gradientDescent(count + 1)
      }else {
        val loss = NeuralNetwork.getLoss(X, y, W, b, activation, regularization)
        lossEvolution += Tuple2(count.toDouble, loss)
        println(s"Training finished after $count epochs with loss " + loss)
      }
    }

    gradientDescent(0)

  }

  def predict(listX: List[List[Double]]): List[Int] = {
    val X: DenseMatrix[Double] = Trafo.toMatrix(listX)
    val output = NeuralNetwork.feedForward(X, W, b, activation)
    val prediction: DenseVector[Int] = argmax(output(*, ::))
    (for (i <- 0 until prediction.size) yield prediction(i)).toList
  }

  override def diagnostics(): Map[String, List[(Double, Double)]] = {
    Map("loss" -> lossEvolution.toList,
        "alpha" -> alphaEvolution.toList
      )
  }

}
