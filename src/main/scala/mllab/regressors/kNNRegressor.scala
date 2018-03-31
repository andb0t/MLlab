package regressors

import play.api.libs.json.JsValue

import json._
import utils._


/** Companion object providing default parameters */
object kNNRegressor {
  val k: Int = 20
}

/** k-nearest neighbors regressor
 * @param k Number of closest neighbors to consider
 */
class kNNRegressor(
  k: Int = kNNRegressor.k
) extends Regressor {
  def this(json: JsValue) = {
    this(
      k = JsonMagic.toInt(json, "k", kNNRegressor.k)
      )
  }

  val name: String = "kNNRegressor"

  var X_NN: List[List[Double]] = Nil
  var y_NN: List[Double] = Nil

  def train(X: List[List[Double]], y: List[Double]): Unit = {
    require(X.length == y.length, "number of training instances and labels is not equal")
    require(X.length >= k, "need more instances than k hyperparameter")
    X_NN = X
    y_NN = y
  }

  /** Yield new collection of nearest neighbors
   * @param x Test instance feature vector
   * @param instance Training instance feature vector
   * @param label Training instance label
   * @param nearest Current collection of nearest neighbors
   */
  def updateNearest(x: List[Double], instance: List[Double], label: Double, nearest: List[Tuple2[Double, Double]]): List[Tuple2[Double, Double]] = {
    val distance: Double = Maths.distance(x, instance)
    val candidate: Tuple2[Double, Double] = (distance, label)
    val maxIdx: Int = nearest.zipWithIndex.maxBy(_._1._1)._2
    if (nearest(maxIdx)._1 > distance)
      nearest.zipWithIndex.map(ni => if (ni._2 != maxIdx) ni._1 else candidate)
    else
      nearest
  }

  /** Gets list of nearest neighbors */
  def getNearest(x: List[Double], X_NN: List[List[Double]], y_NN: List[Double], nearest: List[Tuple2[Double, Double]]): List[Tuple2[Double, Double]] =
    if (X_NN == Nil) nearest
    else getNearest(x, X_NN.tail, y_NN.tail, updateNearest(x, X_NN.head, y_NN.head, nearest))

  /** Predicts a label for a single instance */
  def getPrediction(x: List[Double]): Double = {
    val nearest = getNearest(x, X_NN, y_NN, List.fill(k)(Tuple2(Double.MaxValue, -1)))
    val labels = for (dl <- nearest) yield dl._2
    Maths.mean(labels)
  }

  def predict(X: List[List[Double]]): List[Double] =
    for (instance <- X) yield getPrediction(instance)

}
