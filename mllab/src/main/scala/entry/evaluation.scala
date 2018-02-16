package mllab

object Evaluation{

  def f1(y_pred: List[Int], y_true: List[Int]): Double = {
    val f1: Double = Math.sqrt(precision(y_pred, y_true, false) * recall(y_pred, y_true, false))
    f1
  }

  def precision(y_pred: List[Int], y_true: List[Int], verbose: Boolean=true): Double = {
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
    if (verbose) println("Precision calculation: " + truePositives + " TP and " + falsePositives + " FP" )
    var precision: Double = Double.MaxValue
    if (truePositives + falsePositives != 0) {
      precision = 1.0 * truePositives / (truePositives + falsePositives)
    }
    precision
  }

  def recall(y_pred: List[Int], y_true: List[Int], verbose: Boolean=true): Double = {
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
    if (verbose) println("Recall calculation: " + truePositives + " TP and " + falseNegatives + " FN" )
    var recall: Double = Double.MaxValue
    if (truePositives + falseNegatives != 0) {
      recall = 1.0 * truePositives / (truePositives + falseNegatives)
    }
    recall
  }

  def matrix(y_pred: List[Int], y_true: List[Int], verbose: Boolean=true): Unit = {
    assert (y_pred.length == y_true.length)
    var truePositives: Int = 0
    var falsePositives: Int = 0
    var trueNegatives: Int = 0
    var falseNegatives: Int = 0
    for (i <- 0 until y_pred.length) {
      if (y_true(i) == 1 && y_pred(i) == 1){
        truePositives += 1
      }
      if (y_true(i) == 0 && y_pred(i) == 1){
        falsePositives += 1
      }
      if (y_true(i) == 0 && y_pred(i) == 0){
        trueNegatives += 1
      }
      if (y_true(i) == 1 && y_pred(i) == 0){
        falseNegatives += 1
      }
    }
    println("  P    N")
    println("T " + truePositives + " " + trueNegatives)
    println("F " + falsePositives + " " + falseNegatives)
  }

}
