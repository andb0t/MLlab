package clustering


/** k-Means clustering
 *
 */
class kMeansClustering() extends Clustering {

  val name: String = "kMeansClustering"

  var nFeatures: Int = 3

  def predict(X: List[List[Double]]): List[Int] =
    for (instance <- X) yield (Math.random * nFeatures).toInt

}
