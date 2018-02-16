package mllab

object Evaluation{

  def calculate_precision(y_pred: List[Int], y_true: List[Int]): Double = {
    assert (y_pred.length == y_true.length)
    var truePositives: Int = 0
    var falsePositives: Int = 0
    for (i <- 0 until y_pred.length) {
      if (y_true(i) == 1 && y_pred(i) == 1){
        truePositives += 1
      }
      if (y_true(i) == 0 && y_pred(i) == 1){
        falsePositives += 1
      }
    }
    println("Precision calculation: " + truePositives + " TP and " + falsePositives + " FP" )
    if (truePositives + falsePositives == 0) {
      return Float.MaxValue
    }
    return 1.0 * truePositives / (truePositives + falsePositives)
  }

  def calculate_recall(y_pred: List[Int], y_true: List[Int]): Double = {
    assert (y_pred.length == y_true.length)
    var truePositives: Int = 0
    var falseNegatives: Int = 0
    for (i <- 0 until y_pred.length) {
      if (y_true(i) == 1 && y_pred(i) == 1){
        truePositives += 1
      }
      if (y_true(i) == 1 && y_pred(i) == 0){
        falseNegatives += 1
      }
    }
    println("Recall calculation: " + truePositives + " TP and " + falseNegatives + " FN" )
    if (truePositives + falseNegatives == 0) {
      return Float.MaxValue
    }
    return 1.0 * truePositives / (truePositives + falseNegatives)
  }
}
