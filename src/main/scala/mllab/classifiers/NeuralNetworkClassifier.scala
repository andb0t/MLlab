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
          val output = NeuralNetwork.feedForward(X, W, b, activation)
          val loss = NeuralNetwork.getLossClf(output, y, W, regularization)
          lossEvolution += Tuple2(count.toDouble, loss)
          println("- epoch% 4d: alpha %.2e, loss %.4e".format(count, decayedAlpha, loss))
        }

        // use random instances for training
        val (thisX, thisy) = Trafo.randomizeInstancesInt(X, y, batchSize)
        // forward propagation
        val A = NeuralNetwork.propagateForward(thisX, W, b, activation)
        // backward propagation
        // distance to truth at output layer
        val Z = NeuralNetwork.feedForward(thisX, W, b, activation)
        val outputDelta: DenseMatrix[Double] = NeuralNetwork.getDeltaClf(Z, thisy)
        // determine weight updates for all layers
        val layerUpdates = NeuralNetwork.propagateBack(outputDelta, A, thisX, W, b, activation, regularization)

        def updateWeights(count: Int): Unit = {
          if (count < b.size) {
            W(count) :+= -decayedAlpha *:* layerUpdates(count)._1
            b(count) :+= -decayedAlpha *:* layerUpdates(count)._2
            updateWeights(count + 1)
          }
        }
        updateWeights(0)

        gradientDescent(count + 1)
      }
      else {
        val output = NeuralNetwork.feedForward(X, W, b, activation)
        val loss = NeuralNetwork.getLossClf(output, y, W, regularization)
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
    Map(
      "loss" -> lossEvolution.toList,
      "alpha" -> alphaEvolution.toList
      )
  }

}
