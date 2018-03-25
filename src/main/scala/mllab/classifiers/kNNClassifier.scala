package classifiers

import scala.collection.mutable.ListBuffer

import utils._


/** k-nearest neighbors classifier
 * @param k Number of closest neighbors to consider
 */
class kNNClassifier(k: Int = 3) extends Classifier {

  val name: String = "kNNClassifier"

  var X_NN = new ListBuffer[List[Double]]()
  var y_NN = new ListBuffer[Int]()

  def train(X: List[List[Double]], y: List[Int]): Unit = {
    require(X.length == y.length, "number of training instances and labels is not equal")
    require(X.length >= k, "need more instances than k hyperparameter")
    X.copyToBuffer(X_NN)
    y.copyToBuffer(y_NN)
  }

  def updateNearest(x: List[Double], instance: List[Double], label: Int, nearest: List[Tuple2[Double, Int]]): List[Tuple2[Double, Int]] = {
    val distance: Double = Maths.distance(x, instance)
    val candidate: Tuple2[Double, Int] = (distance, label)
    val maxIdx: Int = nearest.zipWithIndex.maxBy(_._1._1)._2
    if (nearest(maxIdx)._1 > distance)
      nearest.zipWithIndex.map(ni => if (ni._2 != maxIdx) ni._1 else candidate )
    else
      nearest
  }

  def loopEvents(x: List[Double], X_NN: List[List[Double]], y_NN: List[Int], nearest: List[Tuple2[Double, Int]]): List[Tuple2[Double, Int]] =
    if (X_NN == Nil) nearest
    else loopEvents(x, X_NN.tail, y_NN.tail, updateNearest(x, X_NN.head, y_NN.head, nearest))

  def getPrediction(x: List[Double], classes: List[Int]): Int = {
    val nearest = loopEvents(x, X_NN.toList, y_NN.toList, List.fill(k)(Tuple2(Double.MaxValue, -1)))
    val labels = for (dl <- nearest) yield dl._2
    val probab: List[Double] = for (l <- classes.sorted) yield 1.0 * labels.count(_==l) / labels.length
    probab.zipWithIndex.maxBy(_._1)._2
  }

  def predict(X: List[List[Double]]): List[Int] = {
    val classes: List[Int] = y_NN.toSet.toList.sorted
    for (instance <- X) yield getPrediction(instance, classes)
  }

}
