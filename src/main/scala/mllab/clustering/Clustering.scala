package clustering


/** The base class of all clustering algorithms
  *
  * @constructor Create a new clustering algorithm
  */
abstract class Clustering() {

  /** The name of the clustering algorithm */
  val name: String

  /** Performs the training of the classifier
   * @param X List of training instances
   * @param y List of training labels
   */
  def train(X: List[List[Double]], y: List[Int]): Unit

  /** Applies the trained algorithm to a dataset
   * @param X List of data instances
   * @return List of predictions
   */
  def predict(X: List[List[Double]]): List[Int]

  /** Returns the training evolution of the cluster means */
  def clusterMeans(): List[List[List[Double]]]

  /** Provides meta-information on the algorithm
   * @return Map object of metric names and metric values
   */
  def diagnostics(): Map[String, List[(Double, Double)]] = {
    Map()
  }

}
