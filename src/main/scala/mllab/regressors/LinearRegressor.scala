package regressors

import scala.collection.mutable.ListBuffer

import play.api.libs.json.JsValue

import algorithms._
import evaluation._
import json._
import utils._

/** Companion object providing default parameters */
object LinearRegressor {
  val alpha: Double = 1
  val tol: Double = 0.001
  val maxIter: Int = 100
  val degree: Int = 1
}

/** Linear regressor
 * @param alpha Learning rate
 * @param tol Loss tolerance to stop training
 * @param maxIter Maximum number of training iterations
 * @param degree Order of polynomial features to add to the instances (1 for no addition)
 */
class LinearRegressor(
  alpha: Double = LinearRegressor.alpha,
  tol: Double = LinearRegressor.tol,
  maxIter: Int = LinearRegressor.maxIter,
  degree: Int = LinearRegressor.degree
) extends Regressor {
  def this(json: JsValue) = {
    this(
      alpha = JsonMagic.toDouble(json, "alpha", LinearRegressor.alpha),
      tol = JsonMagic.toDouble(json, "tol", LinearRegressor.tol),
      maxIter = JsonMagic.toInt(json, "maxIter", LinearRegressor.maxIter),
      degree = JsonMagic.toInt(json, "degree", LinearRegressor.degree)
      )
  }

  val name: String = "LinearRegressor"

  /** Weights for the linear transformation */
  var weight: List[Double] = Nil

  var lossEvolution = new ListBuffer[(Double, Double)]()

  /** Returns the predicted values */
  def getPredictions(X: List[List[Double]], weight: List[Double]): List[Double] =
    for (instance <- X) yield Maths.dot(weight, 1 :: instance)

  /** Calculates the gradient of the loss function for the given training data */
  def lossGradient(X: List[List[Double]], y: List[Double], weight: List[Double]): List[Double] = {
    // dLoss = d(MSE scaled) = Sum (const * linearDistanceScaled * instanceVector)
    val linDist: List[Double] = for (xy <- X zip y) yield (Maths.dot(weight, 1.0 :: xy._1) - xy._2) / y.length
    val weightGradient: List[Double] = (X zip linDist).map{case (x, d) => x.map(_ * d)}.reduce(Maths.plus(_, _))
    val biasGradient: Double = linDist.sum
    biasGradient::weightGradient
  }

  def _train(X: List[List[Double]], y: List[Double]): Unit = {
    require(X.length == y.length, "both arguments must have the same length")
    val nFeatures = X.head.length

    def findWeights(count: Int, weight: List[Double]): List[Double] = {
      val loss: Double = Evaluation.MSE(getPredictions(X, weight), y)
      lossEvolution += Tuple2(count.toDouble, loss)
      if (loss > tol && count < maxIter) {
        if (count % 100 == 0 || (count < 50 && count % 10 == 0) || (count < 5))
          println("Step% 4d with loss %.4e and weights ".format(count, loss) +
          weight.map(p => "%+.3f".format(p)).mkString(", ")
        )
        val weightUpdate = lossGradient(X, y, weight).map(_ * alpha)
        val newWeight = weight zip weightUpdate map(wu => wu._1 - wu._2)
        findWeights(count + 1, newWeight)
      }else{
        println(s"Final $count with loss %.4e and weights ".format(loss) +
          weight.map(p => "%+.3f".format(p)).mkString(", ")
        )
        weight
      }
    }

    weight = findWeights(0, List.fill(nFeatures+1)(0))
  }

  def _predict(X: List[List[Double]]): List[Double] =
    getPredictions(X, weight)

  def predict(X: List[List[Double]]): List[Double] =
    _predict(DataTrafo.addPolyFeatures(X, degree))

  def train(X: List[List[Double]], y: List[Double]): Unit =
    _train(DataTrafo.addPolyFeatures(X, degree), y)

  override def diagnostics(): Map[String, List[(Double, Double)]] = {
    Map("loss" -> lossEvolution.toList)
  }
}
