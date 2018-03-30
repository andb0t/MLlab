package classifiers

import algorithms._
import utils._


/** Naive Bayes classifier
 * @param model The distribution function to assume for the feature distributions
 * @param priors Prior probabilities to assume for the classes
 * @param alpha Additive (Laplace/Lidstone) smoothing parameter (0 for no smoothing)
 * @param degree Order of polynomial features to add to the instances (1 for no addition)
 */
class NaiveBayesClassifier(model: String="gaussian", priors: List[Double]=Nil, alpha: Double = 1.0, degree: Int=2) extends Classifier {

  val name: String = "NaiveBayesClassifier"

  /** Prior probability for the classes */
  var prior: List[Double] = Nil
  /** Parameters for the likelihood for each class and feature */
  var params: List[List[List[Double]]] = Nil

  if (priors != Nil)
    assert (priors.sum == 1, "provided priors do not add up to unity")

  /** Calculates the per class and per feature likelihood value of a given instance */
  def getLikeli(x: List[Double]): List[List[Double]] =
    if (model == "gaussian")
      for (pClass <- params) yield for (fp <- x zip pClass) yield Maths.normal(fp._1, fp._2.head, fp._2(1))
    else if (model == "triangular")
      for (pClass <- params) yield for (fp <- x zip pClass) yield Maths.triangular(fp._1, fp._2.head, fp._2(1))
    else if (model == "rectangular")
      for (pClass <- params) yield for (fp <- x zip pClass) yield Maths.rectangular(fp._1, fp._2.head, fp._2(1))
    else if (model == "bernoulli")
      for (pClass <- params) yield for (fp <- x zip pClass) yield Maths.bernoulli(fp._1.toInt, fp._2.head)
    else
      Nil

  /** Calculates the probabilities of belonging to a class for a given instance
   *
   * This bases on Bayes theorem: p(C | x) = p(x | C) * p(C) / const
   * using the naive assumption, that p(x0, ..., xn | C) * p(C) = p(C) * p(x0 | C) * ... * p(xn | C).
   * The constant factor is neglected.
   */
  def getProbabs(x: List[Double]): List[Double] =
    if (model == "multinomial")
      for (prpa <- prior zip params.map(_.flatten)) yield prpa._1 * Maths.multinomial(x.map(_.toInt), prpa._2)
    else
      for (pl <- prior zip getLikeli(x)) yield pl._1 * pl._2.product

  def _train(X: List[List[Double]], y: List[Int]): Unit = {
    require(X.length == y.length, "number of training instances and labels is not equal")
    val classes: List[Int] = y.toSet.toList.sorted
    if (priors != Nil) {
      prior = priors
      assert (prior.length == classes.length, "number of provided priors does not match number of classes")
    }
    else {
      println("Determine priors from training set frequencies")
      prior = for (cl <- classes) yield 1.0 * y.count(_==cl) / y.length
    }
    println("Prior:")
    println(prior.zipWithIndex.map{case (p, c) => " - class " + c + ": " + p}.mkString("\n"))
    println(s"Determine $model model parameters from training features")
    params = for (cl <- classes) yield {
      val thisClassX = (X zip y).filter(_._2 == cl).map(_._1)
      val thisClassFeatures = thisClassX.transpose
      val featParams: List[List[Double]] = for (feature <- thisClassFeatures) yield {
        if (model == "gaussian")
          List(Maths.mean(feature), Maths.std(feature))
        else if (model == "triangular")
          List(Maths.mean(feature), 3 * Maths.std(feature))  // assume no values outside 3 std dev
        else if (model == "rectangular")
          List(feature.min, feature.max)
        else if (model == "bernoulli")
          List(1.0 * feature.count(_==0) / feature.length)
        else if (model == "multinomial")
            List((feature.sum + alpha) / (thisClassFeatures.flatten.sum + thisClassFeatures.length * alpha))
        else throw new NotImplementedError("Bayesian model " + model + " not implemented")
      }
      featParams
    }
    println("%s model parameters: ".format(model.capitalize))
    for (cp <- classes zip params) {
      println("- class " + cp._1 + ":")
      for (pi <- cp._2.zipWithIndex)
        println("  - feature " + pi._2 + ": " + pi._1)
    }
  }

  def _predict(X: List[List[Double]]): List[Int] =
    for (instance <- X) yield getProbabs(instance).zipWithIndex.maxBy(_._1)._2

    def predict(X: List[List[Double]]): List[Int] =
      _predict(DataTrafo.addPolyFeatures(X, degree))

    def train(X: List[List[Double]], y: List[Int]): Unit =
      _train(DataTrafo.addPolyFeatures(X, degree), y)

}
