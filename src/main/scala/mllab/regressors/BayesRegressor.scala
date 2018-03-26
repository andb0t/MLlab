package regressors

import scala.collection.mutable.ListBuffer

import breeze.linalg._

import datastructures._
import plotting._
import utils._


/** Bayes regressor
 *
 * following https://stats.stackexchange.com/questions/252577/bayes-regression-how-is-it-done-in-comparison-to-standard-regression
 * @param degree Order of polynomial features to add to the instances (1 for no addition)
 */
class BayesRegressor(degree: Int=1, savePlots: Boolean=true) extends Regressor {

  val name: String = "BayesRegressor"

  var paramA: Double = 0
  var paramB = new ListBuffer[Double]()
  var paramS: Double = 0

  // Parameter likelihood assumptions
  val meanA: Double = 1.0
  val sigmaA: Double = 2.0
  var meanB = new ListBuffer[Double]()
  var sigmaB = new ListBuffer[Double]()
  val sigmaLike: Double = 2

  // gaussian priors for linear parameters
  val priorA = (a: Double) => {Maths.normal(a, meanA, sigmaA)}
  val priorB = (b: List[Double]) => {(b zip (meanB zip sigmaB)).map{case (bi, ms) => Maths.normal(bi, ms._1, ms._2)}}
  // rectangular prior for prior sigma
  val priorS = (s: Double) => {Maths.rectangular(s, 0, sigmaLike)}

  def finiteLog(x: Double): Double =
    if (x == 0) -10000 else Math.log(x)

  def optimize(func: (Double, List[Double], Double) => Double): Tuple3[Double, List[Double], Double] = {

    def maximize(count: Int, maximum: Double, params: List[Double], ranges: List[List[Double]]): List[Double] = {
      val nSteps = 1000
      val numberDimensions: Int = ranges.length
      if (count == nSteps) {
        println(s"- final step $count: optimum %.3f, params ".format(maximum) +
          "(" + params.map(p => Maths.round(p, 3)).mkString(", ") + ")"
        )
        params
      }
      else {
        if (count % 100 == 0 || (count < 50 && count % 10 == 0) || (count < 5))
          println(s"- optimization step $count: optimum %.3e, params ".format(maximum) +
            "(" + params.map(p => Maths.round(p, 3)).mkString(", ") + ")"
          )
        val dimension: Int = scala.util.Random.nextInt(numberDimensions)
        val sign: Int = scala.util.Random.nextInt(2) * 2 - 1
        val step: Double = 1.0 * sign * (ranges(dimension)(1) - ranges(dimension).head) / 100
        // println(s"Step $count: step %.3f in dimension $dimension".format(step))
        val newParams: List[Double] = params.zipWithIndex.map{case (p, i) => if (i == dimension) p + step else p}
        val newMaximum: Double = func(newParams.head, (for (i <- 1 until newParams.length-1) yield newParams(i)).toList, newParams.last)
        // if (newMaximum > maximum) println("New maximum " + maximum + " at " + params)
        if (newMaximum > maximum) maximize(count+1, newMaximum, newParams, ranges)
        else maximize(count+1, maximum, params, ranges)
      }
    }

    val intervals: Int = 3
    val rangeA: List[List[Double]] = List(List(meanA - intervals * sigmaA, meanA + intervals * sigmaA))
    val rangeB: List[List[Double]] = (meanB.toList zip sigmaB.toList).map{case (m, s) => List(m - intervals * s, m + intervals * s)}
    val rangeS: List[List[Double]] = List(List(0, sigmaLike))
    val ranges: List[List[Double]] = rangeA ::: rangeB ::: rangeS
    val startParams: List[Double] = ranges.map(r => Maths.mean(r))
    val params: List[Double] = maximize(0, Double.MinValue, startParams, ranges)
    (params.head, (for (i <- 1 until params.length-1) yield params(i)).toList, params.last)
  }

  def _train(X: List[List[Double]], y: List[Double]): Unit = {
    require(X.length == y.length, "both arguments must have the same length")
    val nFeatures = X.head.length
    // init weights
    for (i <- 0 until nFeatures) {
      meanB += i
      sigmaB += 3 + i
    }
    // likelihood
    val likelihood = (a: Double, b: List[Double], s: Double) => {
      (X zip y).map{case (xi, yi) => finiteLog(Maths.normal(yi, a + Maths.dot(b, xi), s))}.sum
    }
    // posterior
    val posterior = (a: Double, b: List[Double], s: Double) => {
      likelihood(a, b, s) + finiteLog(priorA(a)) + priorB(b).map(pb => finiteLog(pb)).sum + finiteLog(priorS(s))
    }
    // determine maximum likelihood parameters
    val (maxA: Double, maxB: List[Double], maxS: Double) = optimize(posterior)
    paramA = maxA
    maxB.copyToBuffer(paramB)
    paramS = maxS

    println("Final estimated parameter means for Y <- N(A + B * X, S):")
    println("A = %.3f".format(paramA))
    println("B = " + paramB)
    println("S = %.3f".format(paramS))

    if (savePlots) {
      // get equidistant points in this feature for line plotting
      val intervals = 3.0
      val minB = min((meanB zip sigmaB).map{case (m, s) => m - intervals * s})
      val maxB = max((meanB zip sigmaB).map{case (m, s) => m + intervals * s})
      val minX = min(meanA - intervals * sigmaA, minB, 0 - intervals * sigmaLike)
      val maxX = max(meanA + intervals * sigmaA, maxB, 0 + intervals * sigmaLike)
      val equiVec: DenseVector[Double] = linspace(minX, maxX, 200)
      val xEqui: List[Double] = (for (i <- 0 until equiVec.size) yield equiVec(i)).toList
      val equiFeatures = List.fill(nFeatures)(xEqui)
      // plot some distributions
      val valsA = List(xEqui zip (xEqui.map(priorA(_))))
      val valsB = equiFeatures.transpose.map(priorB(_)).transpose.map(xEqui zip _)
      val valsS = List(xEqui zip (xEqui.map(priorS(_))))
      val vals = valsA ::: valsB ::: valsS
      val names = List("A") ::: (for (i <- 0 until nFeatures) yield "B" + i).toList ::: List("S")
      Plotting.plotCurves(vals, names, xlabel= "Value", name= "plots/reg_Bayes_priors.pdf")

      //
      // val valsPosteriorA = xEqui zip (xEqui.map(eq => posterior(eq, paramB, paramS)))
      // val valsPosteriorB = xEqui zip (xEqui.map(eq => posterior(paramA, eq, paramS)))
      // val valsPosteriorS = xEqui zip (xEqui.map(eq => posterior(paramA, paramB, eq)))
      // Plotting.plotCurves(List(valsPosteriorA, valsPosteriorB, valsPosteriorS), List("Posterior(A)", "Posterior(B)", "Posterior(S)"), xlabel= "Value", name= "plots/reg_Bayes_posterior_dep.pdf")



      // val valsLikelihoodA = xEqui zip (xEqui.map(eq => likelihood(eq, paramB, paramS)))
      // val valsLikelihoodB = xEqui zip (xEqui.map(eq => likelihood(paramA, eq, paramS)))
      // val valsLikelihoodS = xEqui zip (xEqui.map(eq => likelihood(paramA, paramB, eq)))
      // Plotting.plotCurves(List(valsLikelihoodA, valsLikelihoodB, valsLikelihoodS), List("Likelihood(A)", "Likelihood(B)", "Likelihood(S)"), xlabel= "Value", name= "plots/reg_Bayes_likelihood_dep.pdf")
    }
  }

  def _predict(X: List[List[Double]]): List[Double] =
    for (instance <- X) yield paramA + Maths.dot(paramB.toList, instance)

  def predict(X: List[List[Double]]): List[Double] =
    _predict(DataTrafo.addPolyFeatures(X, degree))

  def train(X: List[List[Double]], y: List[Double]): Unit =
    _train(DataTrafo.addPolyFeatures(X, degree), y)

}
