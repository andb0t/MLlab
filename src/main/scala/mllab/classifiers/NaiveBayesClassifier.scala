package classifiers

import scala.collection.mutable.ListBuffer


class NaiveBayesClassifier(model: String="gaussian") extends Classifier {

  val name: String = "SVMClassifier"

  /** Prior probability for the classes */
  var prior = new ListBuffer[Double]()
  /** Parameters for the likelihood for each class and feature */
  var params = new ListBuffer[List[List[Double]]]()

  def gaussian(x: Double, m: Double, s: Double): Double =
    Math.exp( -Math.pow((x - m), 2) / (2 * Math.pow(s, 2))) / Math.sqrt(2 * math.Pi * Math.pow(s, 2))

  def mean(l: List[Double]): Double =
    l.sum / l.length

  def variance(l: List[Double]): Double =
      l.map(a => Math.pow(a - mean(l), 2)).sum / l.size

  def stdDev(l: List[Double]): Double =
    Math.sqrt(variance(l))

  def getLikeli(x: List[Double]): List[List[Double]] =
    if (model == "gaussian")
      (for (pClass <- params) yield for (ip <- x zip pClass) yield gaussian(ip._1, ip._2.head ,ip._2(1))).toList
    else throw new NotImplementedError("Bayesian model " + model + " not implemented")

  def getProbabs(x: List[Double]): List[Double] =
    (for (pl <- prior zip getLikeli(x)) yield pl._1 * pl._2.product).toList

  def train(X: List[List[Double]], y: List[Int]): Unit = {
    require(X.length == y.length, "number of training instances and labels is not equal")
    val classes: List[Int] = y.toSet.toList
    val features: List[Int] = (for (i <- 0 until X.head.length) yield i).toList
    for (cl <- classes) {
      prior += 1.0 * y.count(_==cl) / y.length
    }
    println("Prior: " + prior)
    for (cl <- classes) {
      val thisClassX = (X zip y).filter(_._2 == cl).map(_._1)
      println("Class " + cl + " has " + thisClassX.length + " training instances")
      val thisClassFeatures = thisClassX.transpose
      val featParams: List[List[Double]] = for (feature <- thisClassFeatures) yield {
        if (model == "gaussian") List(mean(feature), stdDev(feature))
        else throw new NotImplementedError("Bayesian model " + model + " not implemented")
      }
      params += featParams
    }
    println("Likelihood params: ")
    println(params.mkString("\n"))
  }

  def predict(X: List[List[Double]]): List[Int] = {
    val result = for (instance <- X) yield getProbabs(instance).zipWithIndex.maxBy(_._1)._2
    println((X zip X.map(getProbabs(_)) zip result).take(5).mkString("\n"))
    result
  }
}
