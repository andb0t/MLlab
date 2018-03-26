package regressors

import breeze.linalg._

import plotting._
import utils._


/** Bayes regressor
 *
 * following https://stats.stackexchange.com/questions/252577/bayes-regression-how-is-it-done-in-comparison-to-standard-regression
 */
class BayesRegressor() extends Regressor {

  val name: String = "BayesRegressor"

  var paramA: Double = 0
  var paramB: Double = 0
  var paramS: Double = 0

  def optimize(func: (Double, Double, Double) => Double): Tuple3[Double, Double, Double] = {
    println("Optimizing this function: ", func.getClass)
    Tuple3(-1.0, 2.0, 1.0)
  }

  def train(X: List[List[Double]], y: List[Double]): Unit = {
    require(X.length == y.length, "both arguments must have the same length")

    // restrict regression to one feature
    val oneFeatureX = X.transpose.head

    // Gaussian likelihoods for parameters
    val meanA: Double = -1.0
    val sigmaA: Double = 3.0
    val meanB: Double = 2.0
    val sigmaB: Double = 3.0
    val likeA = (a: Double) => {Maths.normal(a, meanA, sigmaA)}
    val likeB = (b: Double) => {Maths.normal(b, meanB, sigmaB)}
    // Rectangular likelihood for prior sigma
    val sigmaPrior: Double = 1.0
    val likeS = (s: Double) => {Maths.rectangular(s, 0, sigmaPrior)}
    // Gaussian prior
    val prior = (x: List[Double], y: List[Double], a: Double, b: Double, s: Double) => {
      val terms = (x zip y).map{case (xi, yi) => Math.log(Maths.normal(yi, a + b * xi, s))}
      val inftyIndices = terms.zipWithIndex.filter(_._1 < Double.MinValue).map(_._2)
      if (!inftyIndices.isEmpty) {
        val inftyInstances = Trafo.iloc(x zip y, inftyIndices)
        val normalValues = inftyInstances.map(xy => Maths.normal(xy._2, a + b * xy._1, s))
        println("Warning: " + inftyInstances.length + " infinity logs:")
        println(inftyInstances + " -> " + normalValues)
        println("Warning: skip them!")
      }
      terms.filter(_ > Double.MinValue).sum
      // x.map(xi => Maths.normal(y, a + b * xi, s)).product
    }
    // posterior
    val posterior = (a: Double, b: Double, s: Double, x: List[Double], y: List[Double]) => {
      prior(x, y, a, b, s) * likeA(a) * likeB(b) * likeS(s)
    }
    // posterior given the data
    val posteriorPickled = (a: Double, b: Double, s: Double) => {
      posterior(a, b, s, oneFeatureX, y)
    }
    // determine maximum likelihood parameters
    val (maxA: Double, maxB: Double, maxS: Double) = optimize(posteriorPickled)
    paramA = maxA
    paramB = maxB
    paramS = maxS

    // get equidistant points in this feature for line plotting
    val intervals = 3.0
    val minX = min(meanA - intervals * sigmaA, meanB - intervals * sigmaB, 0 - intervals * sigmaPrior)
    val maxX = max(meanA + intervals * sigmaA, meanB + intervals * sigmaB, 0 + intervals * sigmaPrior)
    val equiVec: DenseVector[Double] = linspace(minX, maxX, 200)
    val xEqui: List[Double] = (for (i <- 0 until equiVec.size) yield equiVec(i)).toList
    // plot some distributions
    val valsA = xEqui zip (xEqui.map(likeA(_)))
    val valsB = xEqui zip (xEqui.map(likeB(_)))
    val valsS = xEqui zip (xEqui.map(likeS(_)))
    val valsPosterior = xEqui zip (xEqui.map(eq => Maths.normal(eq, paramA + paramB * eq, paramS)))
    Plotting.plotCurves(List(valsPosterior), List("Posterior"), xlabel= "Value", name= "plots/reg_Bayes_posterior.pdf")
    Plotting.plotCurves(List(valsA, valsB, valsS), List("A", "B", "S"), xlabel= "Value", name= "plots/reg_Bayes_like.pdf")
    // Plotting.plotCurves(List(valsA, valsB, valsS, valsPosterior), List("A", "B", "S", "Posterior"), xlabel= "Value", name= "plots/reg_Bayes_like.pdf")

  }

  def predict(X: List[List[Double]]): List[Double] = {
    // restrict regression to one feature
    val oneFeatureX = X.transpose.head
    for (x <- oneFeatureX) yield paramA + paramB * x
  }

}
