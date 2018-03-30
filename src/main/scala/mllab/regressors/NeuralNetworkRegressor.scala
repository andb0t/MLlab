package regressors

import scala.collection.mutable.ListBuffer

import breeze.linalg._

import utils._
import datastructures._


/** Neural network regressor
 *
 */
class NeuralNetworkRegressor(
  alpha: Double = 0.001,
  alphaHalflife: Int = 100,
  alphaDecay: String = "exp",
  regularization: Double = 0.01,
  activation: String = "tanh",
  batchSize: Int = -1,
  layers: List[Int] = List(1, 5, 1)
) extends Regressor {

  require(layers.length > 2, "too few layers: need at least an input, a middle and an output layer")

  val name: String = "NeuralNetworkRegressor"

  val W: IndexedSeq[DenseMatrix[Double]] = for (i <- 0 until layers.length - 1) yield DenseMatrix.rand[Double](layers(i),layers(i+1))
  val b: IndexedSeq[DenseVector[Double]] = for (i <- 0 until layers.length - 1) yield DenseVector.zeros[Double](layers(i+1))

  var lossEvolution = new ListBuffer[(Double, Double)]()
  var alphaEvolution = new ListBuffer[(Double, Double)]()

  def train(listX: List[List[Double]], listy: List[Double]): Unit = {
    require(listX.length == listy.length, "number of training instances and labels is not equal")
    require(layers.last == 1, "regression needs a single output layer")
    val X: DenseMatrix[Double] = Trafo.toMatrix(listX)
    val y: DenseVector[Double] = Trafo.toVector(listy)

    println("Apply backpropagation gradient descent")
    val maxEpoch: Int = 1000

    def gradientDescent(count: Int): Unit = {

      val decayedAlpha: Double =
        if (alphaDecay == "step") alpha / Math.pow(2, Math.floor(count.toFloat / alphaHalflife))
        else if (alphaDecay == "exp") alpha * Math.exp(-1.0 * count / alphaHalflife)
        else alpha

        alphaEvolution += Tuple2(count.toDouble, decayedAlpha)

        if (count < maxEpoch) {
          if (count % 100 == 0 || (count < 50 && count % 10 == 0) || (count < 5)) {
            val output = NeuralNetwork.feedForward(X, W, b, activation)
            val loss = NeuralNetwork.getLossReg(output, y, W, regularization)
            lossEvolution += Tuple2(count.toDouble, loss)
            println("- epoch% 4d: alpha %.2e, loss %.4e".format(count, decayedAlpha, loss))
          }

          val randomIndices: Seq[Int] =
            if (batchSize != -1)  Seq.fill(batchSize)(scala.util.Random.nextInt(X.rows))
            else 0 until X.rows
          val thisX: DenseMatrix[Double] = X(randomIndices, ::).toDenseMatrix
          val thisy: DenseVector[Double] = y(randomIndices).toDenseVector

          // forward propagation
          val A = NeuralNetwork.propagateForward(thisX, W, b, activation)

          // backward propagation
          val Z = NeuralNetwork.feedForward(thisX, W, b, activation)
          assert (Z.cols == 1)
          val delta: DenseMatrix[Double] = DenseMatrix.tabulate(Z.rows, layers.last){
            case (i, j) => thisy(i) - Z(i, j)
          }
          // determine weight update output layer
          val dWoutputLayer: DenseMatrix[Double] = A(b.size - 2).t * delta + regularization *:* W(b.size - 1)  // (10, 2)
          val dboutputLayer: DenseVector[Double] = sum(delta.t(*, ::))  // (2)
          val updateOutputLayer = Tuple2(dWoutputLayer, dboutputLayer)
          // determine weight updates other layers
          val updateInnerLayers = NeuralNetwork.propagateBack(delta, A, thisX, W, b, activation, regularization)

          def updateWeights(count: Int): Unit = {
            if (count < b.size - 1) {
              W(count) :+= decayedAlpha *:* updateInnerLayers(count)._1
              b(count) :+= decayedAlpha *:* updateInnerLayers(count)._2
              updateWeights(count + 1)
            }
            else{
              W(b.size - 1) :+= decayedAlpha *:* updateOutputLayer._1
              b(b.size - 1) :+= decayedAlpha *:* updateOutputLayer._2
            }
          }
          updateWeights(0)

          gradientDescent(count + 1)
        }
        else {
          val output = NeuralNetwork.feedForward(X, W, b, activation)
          val loss = NeuralNetwork.getLossReg(output, y, W, regularization)
          lossEvolution += Tuple2(count.toDouble, loss)
          println(s"Training finished after $count epochs with loss " + loss)
        }
    }

    gradientDescent(0)

  }

  def predict(listX: List[List[Double]]): List[Double] = {
    val X: DenseMatrix[Double] = Trafo.toMatrix(listX)
    val output = NeuralNetwork.feedForward(X, W, b, activation)
    val prediction: DenseVector[Double] = output(::, 0)
    (for (i <- 0 until prediction.size) yield prediction(i)).toList
  }

  override def diagnostics(): Map[String, List[(Double, Double)]] = {
    Map(
      "loss" -> lossEvolution.toList,
      "alpha" -> alphaEvolution.toList
      )
  }

}
