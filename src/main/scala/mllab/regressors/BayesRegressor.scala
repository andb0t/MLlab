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

  // Parameter likelihood assumptions
  val meanA: Double = -1.0
  val sigmaA: Double = 5.0
  val meanB: Double = 2.0
  val sigmaB: Double = 3.0
  val sigmaPrior: Double = 2

  def optimize(func: (Double, Double, Double) => Double): Tuple3[Double, Double, Double] = {
    println("Optimizing this function: ", func.getClass)
    val nSteps = 100
    val numberDimensions: Int = 3

    def maximize(count: Int, maximum: Double, params: Tuple3[Double, Double, Double], ranges: List[List[Double]]): Tuple3[Double, Double, Double] =
      if (count == nSteps) {
        println(s"- Final step $count: optimum %.3f, params ".format(maximum) + params)
        params
      }
      else {
        if (count % 100 == 0 || (count < 50 && count % 10 == 0) || (count < 5))
          println(s"- optimization step $count: optimum %.3e, params ".format(maximum) + params)
        val dimension: Int = scala.util.Random.nextInt(numberDimensions)
        val sign: Int = scala.util.Random.nextInt(2) * 2 - 1
        val step: Double = 1.0 * sign * (ranges(dimension)(1) - ranges(dimension).head) / 100
        // println(s"Step $count: step %.3f in dimension $dimension".format(step))
        val newParams =
          if (dimension == 0) Tuple3(params._1 + step, params._2, params._3)
          else if (dimension == 1) Tuple3(params._1, params._2 + step, params._3)
          else Tuple3(params._1, params._2, params._3 + step)
        val newMaximum = func(newParams._1, newParams._2, newParams._3)
        // if (newMaximum > maximum) println("New maximum " + maximum + " at " + params)
        if (newMaximum > maximum) maximize(count+1, newMaximum, newParams, ranges)
        else maximize(count+1, maximum, params, ranges)
      }

    val intervals: Int = 3
    val rangeA: List[Double] = List(meanA - intervals * sigmaA, meanA + intervals * sigmaA)
    val rangeB: List[Double] = List(meanB - intervals * sigmaB, meanB + intervals * sigmaB)
    val rangeS: List[Double] = List(0, sigmaPrior)
    val startParams = (Maths.mean(rangeA), Maths.mean(rangeB), Maths.mean(rangeS))
    val ranges: List[List[Double]] = List(rangeA, rangeB, rangeS)
    // maximize(0, Double.MinValue, startParams, ranges)
    maximize(0, Double.MinValue, (0.0, 0.0, 0.1), ranges)
  }

  def train(X: List[List[Double]], y: List[Double]): Unit = {
    require(X.length == y.length, "both arguments must have the same length")

    // restrict regression to one feature
    val oneFeatureX = X.transpose.head

    // Gaussian likelihood for parameters
    val likeA = (a: Double) => {Maths.normal(a, meanA, sigmaA)}
    val likeB = (b: Double) => {Maths.normal(b, meanB, sigmaB)}
    // Rectangular likelihood for prior sigma
    val likeS = (s: Double) => {Maths.rectangular(s, 0, sigmaPrior)}
    // Gaussian prior
    val prior = (x: List[Double], y: List[Double], a: Double, b: Double, s: Double) => {
      val normals = (x zip y).map{case (xi, yi) => Maths.normal(yi, a + b * xi, s)}
      val logs = normals.map(n => if (n == 0) 1 else n).map(n => Math.log(n))
      -logs.filter(_ > Double.MinValue).sum
        // x.map(xi => Maths.normal(y, a + b * xi, s)).product
    }
    // prior given the data
    val priorPickled = (a: Double, b: Double, s: Double) => {
      prior(oneFeatureX, y, a, b, s)
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

    println("Final posterior value: ", posteriorPickled(paramA, paramB, paramS))
    println("Posterior value correct: ", posteriorPickled(meanA, meanB, sigmaPrior))

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
    val valsPosteriorPickledA = xEqui zip (xEqui.map(eq => posteriorPickled(eq, paramB, paramS)))
    val valsPosteriorPickledB = xEqui zip (xEqui.map(eq => posteriorPickled(paramA, eq, paramS)))
    val valsPosteriorPickledS = xEqui zip (xEqui.map(eq => posteriorPickled(paramA, paramB, eq)))
    val valsPriorPickledA = xEqui zip (xEqui.map(eq => priorPickled(eq, paramB, paramS)))
    val valsPriorPickledB = xEqui zip (xEqui.map(eq => priorPickled(paramA, eq, paramS)))
    val valsPriorPickledS = xEqui zip (xEqui.map(eq => priorPickled(paramA, paramB, eq)))

    Plotting.plotCurves(List(valsPosteriorPickledA, valsPosteriorPickledB, valsPosteriorPickledS), List("Posterior(A)", "Posterior(B)", "Posterior(S)"), xlabel= "Value", name= "plots/reg_Bayes_posterior_dep.pdf")
    Plotting.plotCurves(List(valsPriorPickledA, valsPriorPickledB, valsPriorPickledS), List("Prior(A)", "Prior(B)", "Prior(S)"), xlabel= "Value", name= "plots/reg_Bayes_prior_dep.pdf")
    Plotting.plotCurves(List(valsPosterior), List("Posterior"), xlabel= "Value", name= "plots/reg_Bayes_posterior.pdf")
    Plotting.plotCurves(List(valsA, valsB, valsS), List("A", "B", "S"), xlabel= "Value", name= "plots/reg_Bayes_like.pdf")

    println("Final estimated parameter means for y <- N(A + B * x, S):")
    println("A: " + paramA)
    println("B: " + paramB)
    println("S: " + paramS)
  }

  def predict(X: List[List[Double]]): List[Double] = {
    // restrict regression to one feature
    val oneFeatureX = X.transpose.head
    for (x <- oneFeatureX) yield paramA + paramB * x
  }

}