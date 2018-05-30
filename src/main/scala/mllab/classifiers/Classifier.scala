package classifiers


/** The base class of all classifiers
  *
  * @constructor Create a new classifier
  */
abstract class Classifier() {

  /** The name of the classifier */
  val name: String

  /** Performs the training of the classifier
   * @param X List of training instances
   * @param y List of training labels
   * @param w Optional list of instance weights (not implemented in all classifiers)
   */
  def train(X: List[List[Double]], y: List[Int], sampleWeight: List[Double] = Nil): Unit

  /** Applies the trained classifier to a dataset
   * @param X List of data instances
   * @return List of predictions
   */
  def predict(X: List[List[Double]]): List[Int]

  /** Provides meta-information on the classifier
   * @return Map object of metric names and metric values
   */
  def diagnostics(): Map[String, List[(Double, Double)]] = {
    Map()
  }

}
