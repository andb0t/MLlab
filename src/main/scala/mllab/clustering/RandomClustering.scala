package clustering


/** Random clustering
 *
 * This is a clustering algorithm deciding randomly on the output class
 */
class RandomClustering() extends Clustering {

  val name: String = "RandomClustering"

  var nFeatures: Int = 3

  def predict(X: List[List[Double]]): List[Int] =
    for (instance <- X) yield (Math.random * nFeatures).toInt

}
