package regressors


/**
  * The base class of all regressors
  *
  * @constructor Create a new regressor
  */
abstract class Regressor() {

  /** The name of the regressor */
  val name: String

  /**
   * Performs the training of the regressor
   * @param X List of training instances
   * @param y List of training labels
   */
  def train(X: List[List[Double]], y: List[Double]): Unit

  /**
   * Applies the trained regressor to a dataset
   * @param X List of data instances
   * @return List of predictions
   */
  def predict(X: List[List[Double]]): List[Double]
}
