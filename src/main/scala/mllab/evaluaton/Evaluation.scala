package evaluation


/** Provides functions for evaluation of algorithm performance */
object Evaluation{

  /** Calculates the F1 score
   *@param y_pred List of predictions
   *@param y_true List of labels
   */
  def f1(y_pred: List[Int], y_true: List[Int]): Double =
    Math.sqrt(precision(y_pred, y_true) * recall(y_pred, y_true))

  /** Calculates the precision score
   *@param y_pred List of predictions
   *@param y_true List of labels
   */
  def precision(y_pred: List[Int], y_true: List[Int]): Double = {
    require(y_pred.length == y_true.length, "both arguments must have the same length")
    val truePositives = (y_pred zip y_true) count (_ == (1, 1))
    val falsePositives = (y_pred zip y_true) count (_ == (1, 0))
    // println("Precision calculation: " + truePositives + " TP and " + falsePositives + " FP" )
    if (truePositives + falsePositives != 0) 1.0 * truePositives / (truePositives + falsePositives)
    else Double.NaN
  }

  /** Calculates the recall score
   *@param y_pred List of predictions
   *@param y_true List of labels
   */
  def recall(y_pred: List[Int], y_true: List[Int]): Double = {
    require(y_pred.length == y_true.length, "both arguments must have the same length")
    val truePositives = (y_pred zip y_true) count (_ == (1, 1))
    val falseNegatives = (y_pred zip y_true) count (_ == (0, 1))
    // println("Recall calculation: " + truePositives + " TP and " + falseNegatives + " FN" )
    if (truePositives + falseNegatives != 0) 1.0 * truePositives / (truePositives + falseNegatives)
    else Double.MaxValue
  }

  /** Prints the confusion matrix
   *@param y_pred List of predictions
   *@param y_true List of labels
   */
  def matrix(y_pred: List[Int], y_true: List[Int]): Unit = {
    require(y_pred.length == y_true.length, "both arguments must have the same length")
    val truePositives = (y_pred zip y_true) count (_ == (1, 1))
    val falsePositives = (y_pred zip y_true) count (_ == (1, 0))
    val trueNegatives = (y_pred zip y_true) count (_ == (0, 0))
    val falseNegatives = (y_pred zip y_true) count (_ == (0, 1))
    println("  P    N")
    println("T " + truePositives + " " + trueNegatives)
    println("F " + falsePositives + " " + falseNegatives)
  }

  /** Calculates the Mean Squared Error
   *@param y_pred List of predictions
   *@param y_true List of labels
   */
  def MSE(y_pred: List[Double], y_true: List[Double]): Double =
    (y_pred zip y_true).map{case (x, y) => Math.pow(x - y, 2)}.sum / y_true.length

  /** Calculates the log loss
   *@param p_pred List of probabilities for each instance
   *@param y_true List of labels
   *@param eps Clip probabilities to avoid undefined logs at p = 0, 1
   */
  def LogLoss(p_pred: List[Double], y_true: List[Int], eps: Double=1e-15): Double = {
    def clipped(p: Double): Double =
      Math.max(eps, Math.min(1 - eps, p))
    - (p_pred.map(clipped) zip y_true).map{case (p, y) => y * Math.log(p) + (1 - y) * Math.log(1 - p)}.sum / y_true.length
  }

}
