package classifiers

import play.api.libs.json.JsValue

import json._
import utils._


/** Companion object providing default parameters */
object kNNClassifier {
  val k: Int = 5
}

/** k-nearest neighbors classifier
 * @param k Number of closest neighbors to consider
 */
class kNNClassifier(
  k: Int = kNNClassifier.k
) extends Classifier {
  def this(json: JsValue) = {
    this(
      k = JsonMagic.toInt(json, "k", kNNClassifier.k)
      )
  }

  val name: String = "kNNClassifier"

  var X_NN: List[List[Double]] = Nil
  var y_NN: List[Int] = Nil

  def train(X: List[List[Double]], y: List[Int]): Unit = {
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
  def updateNearest(x: List[Double], instance: List[Double], label: Int, nearest: List[Tuple2[Double, Int]]): List[Tuple2[Double, Int]] = {
    val distance: Double = Maths.distance(x, instance)
    val candidate: Tuple2[Double, Int] = (distance, label)
    val maxIdx: Int = nearest.zipWithIndex.maxBy(_._1._1)._2
    if (nearest(maxIdx)._1 > distance)
      nearest.zipWithIndex.map(ni => if (ni._2 != maxIdx) ni._1 else candidate)
    else
      nearest
  }

  /** Gets list of nearest neighbors */
  def getNearest(x: List[Double], X_NN: List[List[Double]], y_NN: List[Int], nearest: List[Tuple2[Double, Int]]): List[Tuple2[Double, Int]] =
    if (X_NN == Nil) nearest
    else getNearest(x, X_NN.tail, y_NN.tail, updateNearest(x, X_NN.head, y_NN.head, nearest))

  /** Predicts a label for a single instance */
  def getPrediction(x: List[Double]): Int = {
    val nearest = getNearest(x, X_NN, y_NN, List.fill(k)(Tuple2(Double.MaxValue, -1)))
    val labels: List[Int] = for (dl <- nearest) yield dl._2
    val occurences: Map[Int, Int] = labels.foldLeft(Map.empty[Int, Int]){(m, c) => m + ((c, m.getOrElse(c, 0) + 1))}
    val maxClass: Int = occurences.maxBy(_._2)._1
    val maxClasses: List[Int] = (occurences.filter(_._2 == occurences(maxClass)).map(_._1)).toList
    val randIdx: Int = scala.util.Random.nextInt(maxClasses.length)
    maxClasses(randIdx)
  }

  def predict(X: List[List[Double]]): List[Int] =
    for (instance <- X) yield getPrediction(instance)

}
