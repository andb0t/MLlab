package classifiers

import scala.collection.mutable.ListBuffer

import utils._


class NaiveBayesClassifier(model: String="gaussian", priors: List[Double]=Nil) extends Classifier {

  val name: String = "NaiveBayesClassifier"

  /** Prior probability for the classes */
  var prior = new ListBuffer[Double]()
  /** Parameters for the likelihood for each class and feature */
  var params = new ListBuffer[List[List[Double]]]()

  if (priors != Nil)
    assert (priors.sum == 1, "provided priors do not add up to unity")

  /** Calculates the per class and per feature likelihood value of a given instance */
  def getLikeli(x: List[Double]): List[List[Double]] =
    if (model == "gaussian")
      (for (pClass <- params) yield for (fp <- x zip pClass) yield Maths.norm(fp._1, fp._2.head, fp._2(1))).toList
    else if (model == "multinomial")
      (for (pClass <- params) yield for (fp <- x zip pClass) yield 0.0).toList
    else if (model == "bernoulli")
      (for (pClass <- params) yield for (fp <- x zip pClass) yield Maths.bernoulli(fp._1.toInt, fp._2.head)).toList
    else throw new NotImplementedError("Bayesian model " + model + " not implemented")

  /** Calculates the probabilities of belonging to a class for a given instance
   *
   * This bases on Bayes theorem: p(C | x) = p(x | C) * p(C) / const
   * using the naive assumption, that p(x0, ..., xn | C) * p(C) = p(C) * p(x0 | C) * ... * p(xn | C).
   * The constant factor is neglected.
   */
  def getProbabs(x: List[Double]): List[Double] =
    (for (pl <- prior zip getLikeli(x)) yield pl._1 * pl._2.product).toList

  def train(X: List[List[Double]], y: List[Int]): Unit = {
    require(X.length == y.length, "number of training instances and labels is not equal")
    val classes: List[Int] = y.toSet.toList
    if (priors != Nil) {
      for (p <- priors) prior += p
      assert (prior.length == classes.length, "number of provided priors does not match number of classes")
    }
    else {
      println("Determine priors from training set frequencies")
      for (cl <- classes) {
        prior += 1.0 * y.count(_==cl) / y.length
      }
    }
    println("Prior:")
    println(prior.zipWithIndex.map{case (p, c) => " - class " + c + ": " + p}.mkString("\n"))
    println("Determine model parameters from training features")
    for (cl <- classes) {
      val thisClassX = (X zip y).filter(_._2 == cl).map(_._1)
      val thisClassFeatures = thisClassX.transpose
      val featParams: List[List[Double]] = for (feature <- thisClassFeatures) yield {
        if (model == "gaussian") List(Maths.mean(feature), Maths.std(feature))
        else if (model == "multinomial") List(0.0)
        else if (model == "bernoulli") List(1.0 * feature.count(_==0) / feature.length)
        else throw new NotImplementedError("Bayesian model " + model + " not implemented")
      }
      params += featParams
    }
    println("Model parameters: ")
    for (cp <- classes zip params) {
      println("- class " + cp._1 + ":")
      for (pi <- cp._2.zipWithIndex)
        println("  - feature " + pi._2 + ": " + pi._1)
    }
  }

  def predict(X: List[List[Double]]): List[Int] =
    for (instance <- X) yield getProbabs(instance).zipWithIndex.maxBy(_._1)._2
}
