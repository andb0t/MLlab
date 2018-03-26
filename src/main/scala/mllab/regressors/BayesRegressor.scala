package regressors

import breeze.linalg._

import datastructures._
import plotting._
import utils._


/** Bayes regressor
 *
 * following https://stats.stackexchange.com/questions/252577/bayes-regression-how-is-it-done-in-comparison-to-standard-regression
 *
 * The priors can be set using the priorPars parameter. If left empty, random values will be used.
 * e.g. List(List(0, 1), List(1, 1), List(2)) for Gaussian priors for the intercept (mean 0, sigma 1) and
 * the weights (mean 1, sigma 1) and with the posterior width 2
 *
 * @param degree Order of polynomial features to add to the instances (1 for no addition)
 * @param model The shape of the prior function
 * @param priorPars Parameters of the prior probability density functions
 * @param randInit If set to true, initialize the prior pdf parameters randomly
 */
class BayesRegressor(degree: Int=1, model: String= "gaussian", priorPars: List[List[Double]]= Nil, randInit: Boolean= false) extends Regressor {

  val name: String = "BayesRegressor"

  var paramB: List[Double] = Nil
  var paramS: Double = 0

  // Parameter likelihood assumptions
  var sigmaLike: Double = 0
  var meanB: List[Double] = Nil
  var sigmaB: List[Double] = Nil

  lazy val defaultPars: Map[String, Double] =
    Map("gaussian" -> 1.0,
    "rectangular" -> 1.0)

  lazy val randomPars: Map[String, Double] =
    Map("gaussian" -> (1.0 * scala.util.Random.nextInt(3) + 1),
    "rectangular" -> (1.0 * scala.util.Random.nextInt(3) + 1))

  lazy val customPars: Map[String, Double] =
    Map("gaussian" -> priorPars.last.head,
    "rectangular" -> priorPars.last.head)

  def priorFunc(model: String, x: Double, params: List[Double]): Double =
    if (model == "gaussian") Maths.normal(x, params.head, params.last)
    else if (model == "rectangular") Maths.rectangular(x, params.head, params.last)
    else throw new NotImplementedError("model " + model + " is not implemented")

  // gaussian priors for linear parameters
  val priorB = (b: List[Double]) => {(b zip (meanB zip sigmaB)).map{case (bi, ms) => priorFunc(model, bi, List(ms._1, ms._2))}}
  // rectangular prior for prior sigma
  val priorS = (s: Double) => {Maths.rectangular(s, 0, sigmaLike)}

  def initWeights(model: String, nFeatures: Int): Unit = {
    if (!priorPars.isEmpty) sigmaLike = customPars(model)
    else if (randInit) sigmaLike = randomPars(model)
    else sigmaLike = defaultPars(model)

    if (model == "gaussian")
      if (priorPars.isEmpty) {
        if (randInit){
          meanB = (for (i <- 0 to nFeatures) yield 1.0 * scala.util.Random.nextInt(7) - 3).toList
          sigmaB = (for (i <- 0 to nFeatures) yield 1.0 * scala.util.Random.nextInt(3) + 1).toList
        }
        else {
          meanB = (for (i <- 0 to nFeatures) yield Math.ceil(1.0 * i / 2) * ((i % 2) * 2 - 1)).toList
          sigmaB = (for (i <- 0 to nFeatures) yield 1.0 * (i % 4 + 1)).toList
        }
      }
      else {
        meanB = (for (i <- 0 to nFeatures) yield priorPars(1 + i).head).toList
        sigmaB = (for (i <- 0 to nFeatures) yield priorPars(1 + i).last).toList
      }
    else if (model == "rectangular") {
      if (randInit){
        meanB = (for (i <- 0 to nFeatures) yield -1.0 * scala.util.Random.nextInt(7) - 1).toList
        sigmaB = (for (i <- 0 to nFeatures) yield 1.0 * scala.util.Random.nextInt(7) + 1).toList
      }
      else {
        meanB = (for (i <- 0 to nFeatures) yield Math.ceil(1.0 * i / 2) * ((i % 2) * 2 - 1)).toList
        sigmaB = (for (i <- 0 to nFeatures) yield meanB(i) + 5 + 1.0 * (i % 4 + 1)).toList
      }
    }
    else {
      meanB = (for (i <- 0 to nFeatures) yield priorPars(1 + i).head).toList
      sigmaB = (for (i <- 0 to nFeatures) yield priorPars(1 + i).last).toList
    }
  }

  /** Takes the log with a lower limit */
  def finiteLog(x: Double): Double =
    if (x == 0) -10000 else Math.log(x)

  /** Determine the function maximum by random walks */
  def optimize(func: (List[Double], Double) => Double): Tuple2[List[Double], Double] = {

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
        val newMaximum: Double = func((for (i <- 0 until newParams.length-1) yield newParams(i)).toList, newParams.last)
        // if (newMaximum > maximum) println("New maximum " + maximum + " at " + params)
        if (newMaximum > maximum) maximize(count+1, newMaximum, newParams, ranges)
        else maximize(count+1, maximum, params, ranges)
      }
    }

    val intervals: Int = 3
    val rangeB: List[List[Double]] = (meanB zip sigmaB).map{case (m, s) => List(m - intervals * s, m + intervals * s)}
    val rangeS: List[List[Double]] = List(List(0, sigmaLike))
    val ranges: List[List[Double]] = rangeB ::: rangeS
    val startParams: List[Double] = ranges.map(r => Maths.mean(r))
    val params: List[Double] = maximize(0, Double.MinValue, startParams, ranges)
    ((for (i <- 0 until params.length-1) yield params(i)).toList, params.last)
  }

  def _train(X: List[List[Double]], y: List[Double]): Unit = {
    require(X.length == y.length, "both arguments must have the same length")
    val nFeatures = X.head.length
    initWeights(model, nFeatures)
    // likelihood
    val likelihood = (b: List[Double], s: Double) => {
      (X zip y).map{case (xi, yi) => finiteLog(Maths.normal(yi, Maths.dot(b, 1 :: xi), s))}.sum
    }
    // posterior
    val posterior = ( b: List[Double], s: Double) => {
      likelihood(b, s) + priorB(b).map(pb => finiteLog(pb)).sum + finiteLog(priorS(s))
    }
    // determine maximum likelihood parameters
    val (optimalB: List[Double], optimalS: Double) = optimize(posterior)
    paramB = optimalB
    paramS = optimalS

    println("Final estimated parameter means for Y <- N(B.head + B.tail * X, S):")
    println("B = " + paramB)
    println("S = %.3f".format(paramS))

    // get equidistant points in this feature for line plotting
    val intervals = 3.0
    val minB = min((meanB zip sigmaB).map{case (m, s) => m - intervals * s})
    val maxB = max((meanB zip sigmaB).map{case (m, s) => m + intervals * s})
    val minX = min(minB, 0 - intervals * sigmaLike)
    val maxX = max(maxB, 0 + intervals * sigmaLike)
    val equiVec: DenseVector[Double] = linspace(minX, maxX, 200)
    val xEqui: List[Double] = (for (i <- 0 until equiVec.size) yield equiVec(i)).toList
    val equiFeatures = List.fill(nFeatures+1)(xEqui)
    // plot some distributions
    val valsB = equiFeatures.transpose.map(priorB(_)).transpose.map(xEqui zip _)
    val valsS = xEqui zip (xEqui.map(priorS(_)))
    val vals = valsB ::: List(valsS)
    val names = (for (i <- 0 to nFeatures) yield "B" + i).toList ::: List("S")
    Plotting.plotCurves(vals, names, xlabel= "Value", name= "plots/reg_Bayes_priors.pdf")

    val valsPosteriorB =
      (for (i <- 0 to nFeatures) yield xEqui zip (xEqui.map(eq => {
          val eqB: List[Double] = (for (j <- 0 to nFeatures) yield if (i == j) eq else paramB(j)).toList
          posterior(eqB, paramS)
        }
      ))).toList
    val valsPosteriorS = xEqui zip (xEqui.map(eq => posterior(paramB, eq)))
    val valsPosterior = valsPosteriorB ::: List(valsPosteriorS)
    val namesPosterior = names.map("Posterior(" + _ + ")")
    Plotting.plotCurves(valsPosterior, namesPosterior, xlabel= "Value", name= "plots/reg_Bayes_posterior_dep.pdf")

    val valsLikelihoodB =
      (for (i <- 0 to nFeatures) yield xEqui zip (xEqui.map(eq => {
          val eqB: List[Double] = (for (j <- 0 to nFeatures) yield if (i == j) eq else paramB(j)).toList
          likelihood(eqB, paramS)
        }
      ))).toList
    val valsLikelihoodS = xEqui zip (xEqui.map(eq => likelihood(paramB, eq)))
    val valsLikelihood = valsLikelihoodB ::: List(valsLikelihoodS)
    val namesLikelihood = names.map("Likelihood(" + _ + ")")
    Plotting.plotCurves(valsLikelihood, namesLikelihood, xlabel= "Value", name= "plots/reg_Bayes_likelihood_dep.pdf")
  }

  def _predict(X: List[List[Double]]): List[Double] =
    for (instance <- X) yield Maths.dot(paramB, 1 :: instance)

  def predict(X: List[List[Double]]): List[Double] =
    _predict(DataTrafo.addPolyFeatures(X, degree))

  def train(X: List[List[Double]], y: List[Double]): Unit =
    _train(DataTrafo.addPolyFeatures(X, degree), y)

}
