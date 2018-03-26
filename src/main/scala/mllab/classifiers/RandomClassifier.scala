package classifiers


/** Random classifier
 *
 * This is a classifier deciding randomly on the output class
 */
class RandomClassifier() extends Classifier {

  val name: String = "RandomClassifier"

  var nFeatures: Int = 0

  def train(X: List[List[Double]], y: List[Int]): Unit = {
    require(X.length == y.length, "number of training instances and labels is not equal")
    nFeatures = y.toSet.size
  }

  def predict(X: List[List[Double]]): List[Int] =
    for (instance <- X) yield (Math.random * nFeatures).toInt

}
