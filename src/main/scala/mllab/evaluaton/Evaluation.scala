package evaluation

object Evaluation{

  def f1(y_pred: List[Int], y_true: List[Int]): Double =
    Math.sqrt(precision(y_pred, y_true) * recall(y_pred, y_true))

  def precision(y_pred: List[Int], y_true: List[Int]): Double = {
    require(y_pred.length == y_true.length, "both arguments must have the same length")
    val truePositives = (y_pred zip y_true) count (_ == (1, 1))
    val falsePositives = (y_pred zip y_true) count (_ == (1, 0))
    // println("Precision calculation: " + truePositives + " TP and " + falsePositives + " FP" )
    if (truePositives + falsePositives != 0) 1.0 * truePositives / (truePositives + falsePositives)
    else Double.NaN
  }

  def recall(y_pred: List[Int], y_true: List[Int]): Double = {
    require(y_pred.length == y_true.length, "both arguments must have the same length")
    val truePositives = (y_pred zip y_true) count (_ == (1, 1))
    val falseNegatives = (y_pred zip y_true) count (_ == (0, 1))
    // println("Recall calculation: " + truePositives + " TP and " + falseNegatives + " FN" )
    if (truePositives + falseNegatives != 0) 1.0 * truePositives / (truePositives + falseNegatives)
    else Double.MaxValue
  }

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

  def MSE(y_pred: List[Float], y_true: List[Float]): Float =
    (y_pred zip y_true).map{case (x, y) => Math.pow(x - y, 2)}.sum.toFloat
}
