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

  def getPrediction(x: List[Double], classes: List[Int]): Int = {
    var nearest = new ListBuffer[Tuple2[Double, Int]]()
    for (_ <- 0 until k) nearest += Tuple2(Double.MaxValue, -1)

    def queueNewMinimum(distance: Double, label: Int): Unit = {
      val maxIdx = nearest.zipWithIndex.maxBy(_._1._1)._2
      nearest(maxIdx) = (distance, label)
    }

    for (xy <- X_NN zip y_NN) {
      val instance: List[Double] = xy._1
      val label: Int = xy._2
      val distance = Maths.distance(x, instance)
      queueNewMinimum(distance, label)
    }

    val labels = for (dl <- nearest) yield dl._2
    val probab: List[Double] = for (l <- classes.sorted) yield 1.0 * labels.count(_==l) / labels.length
    probab.zipWithIndex.maxBy(_._1)._2
  }

  def predict(X: List[List[Double]]): List[Int] = {
    val classes: List[Int] = y_NN.toSet.toList.sorted
    for (instance <- X) yield getPrediction(instance, classes)
  }

}
