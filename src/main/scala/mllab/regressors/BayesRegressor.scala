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
 * the other weights (mean 1, sigma 1) and with the posterior width 2
 *
 * @param degree Order of polynomial features to add to the instances (1 for no addition)
 * @param model The shape of the prior function
 * @param priorPars Parameters of the prior probability density functions
 * @param randInit If set to true, initialize the prior pdf parameters randomly
 */
class BayesRegressor(degree: Int=1, model: String= "gaussian", priorPars: List[List[Double]]= Nil, randInit: Boolean= false) extends Regressor {

  val name: String = "BayesRegressor"

  /** The weight vector to be determined by the training */
  var weight: List[Double] = Nil
  /** The posterior width to be determined by the training */
  var width: Double = 0

  // Parameter likelihood assumptions
  var widthPrior: Double = 0
  var weightPrior: List[Double] = Nil
  var weightPriorWidth: List[Double] = Nil

  def priorFunc(model: String, x: Double, params: List[Double]): Double =
    if (model == "gaussian") Maths.normal(x, params.head, params.last)
    else if (model == "rectangular") Maths.rectangular(x, params.head - params.last, params.head + params.last)
    else throw new NotImplementedError("model " + model + " is not implemented")

  // gaussian priors for linear parameters
  val evalWeightPrior = (b: List[Double]) => {(b zip (weightPrior zip weightPriorWidth)).map{case (bi, ms) => priorFunc(model, bi, List(ms._1, ms._2))}}
  // rectangular prior for prior sigma
  val evalWidthPrior = (s: Double) => {Maths.rectangular(s, 0, widthPrior)}

  def initWeights(nFeatures: Int): Unit = {
    if (priorPars.isEmpty) {
      if (randInit){
        widthPrior = 1.0 * scala.util.Random.nextInt(3) + 1
        weightPrior = (for (i <- 0 to nFeatures) yield 1.0 * scala.util.Random.nextInt(7) - 3).toList
        weightPriorWidth = (for (i <- 0 to nFeatures) yield 1.0 * scala.util.Random.nextInt(3) + 1).toList
      }
      else {
        widthPrior = 1.0
        weightPrior = (for (i <- 0 to nFeatures) yield Math.ceil(1.0 * i / 2) * ((i % 2) * 2 - 1)).toList
        weightPriorWidth = (for (i <- 0 to nFeatures) yield 1.0 * (i % 4 + 1)).toList
      }
    }
    else {
      widthPrior = priorPars.last.head
      weightPrior = (for (i <- 0 to nFeatures) yield priorPars(1 + i).head).toList
      weightPriorWidth = (for (i <- 0 to nFeatures) yield priorPars(1 + i).last).toList
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
    val rangeWeight: List[List[Double]] = (weightPrior zip weightPriorWidth).map{case (m, s) => List(m - intervals * s, m + intervals * s)}
    val rangeWidth: List[List[Double]] = List(List(0, widthPrior))
    val ranges: List[List[Double]] = rangeWeight ::: rangeWidth
    val startParams: List[Double] = ranges.map(r => Maths.mean(r))
    val params: List[Double] = maximize(0, Double.MinValue, startParams, ranges)
    ((for (i <- 0 until params.length-1) yield params(i)).toList, params.last)
  }

  def _train(X: List[List[Double]], y: List[Double]): Unit = {
    require(X.length == y.length, "both arguments must have the same length")
    val nFeatures = X.head.length
    initWeights(nFeatures)
    // likelihood
    val likelihood = (W: List[Double], s: Double) => {
      (X zip y).map{case (xi, yi) => finiteLog(Maths.normal(yi, Maths.dot(W, 1 :: xi), s))}.sum
    }
    // posterior
    val posterior = (W: List[Double], s: Double) => {
      likelihood(W, s) + evalWeightPrior(W).map(wp => finiteLog(wp)).sum + finiteLog(evalWidthPrior(s))
    }
    // determine maximum likelihood parameters
    val (optimalWeight: List[Double], optimalWidth: Double) = optimize(posterior)
    weight = optimalWeight
    width = optimalWidth

    println("Final estimated parameter means for Y <- N(B.head + B.tail * X, S):")
    println("B = " + weight)
    println("S = %.3f".format(width))

    // get equidistant points in this feature for line plotting
    val intervals = 3.0
    val minWeight = min((weightPrior zip weightPriorWidth).map{case (m, s) => m - intervals * s})
    val maxWeight = max((weightPrior zip weightPriorWidth).map{case (m, s) => m + intervals * s})
    val minX = min(minWeight, 0 - intervals * widthPrior)
    val maxX = max(maxWeight, 0 + intervals * widthPrior)
    val equiVec: DenseVector[Double] = linspace(minX, maxX, 200)
    val xEqui: List[Double] = (for (i <- 0 until equiVec.size) yield equiVec(i)).toList
    val equiFeatures = List.fill(nFeatures+1)(xEqui)
    // plot some distributions
    val valsWeight = equiFeatures.transpose.map(evalWeightPrior(_)).transpose.map(xEqui zip _)
    val valsWidth = xEqui zip (xEqui.map(evalWidthPrior(_)))
    val vals = valsWeight ::: List(valsWidth)
    val names = (for (i <- 0 to nFeatures) yield "B" + i).toList ::: List("S")
    Plotting.plotCurves(vals, names, xlabel= "Value", name= "plots/reg_Bayes_priors.pdf")

    val valsPosteriorWeight =
      (for (i <- 0 to nFeatures) yield xEqui zip (xEqui.map(eq => {
          val eqB: List[Double] = (for (j <- 0 to nFeatures) yield if (i == j) eq else weight(j)).toList
          posterior(eqB, width)
        }
      ))).toList
    val valsPosteriorWidth = xEqui zip (xEqui.map(eq => posterior(weight, eq)))
    val valsPosterior = valsPosteriorWeight ::: List(valsPosteriorWidth)
    val namesPosterior = names.map("Posterior(" + _ + ")")
    Plotting.plotCurves(valsPosterior, namesPosterior, xlabel= "Value", name= "plots/reg_Bayes_posterior_dep.pdf")

    val valsLikelihoodWeight =
      (for (i <- 0 to nFeatures) yield xEqui zip (xEqui.map(eq => {
          val eqB: List[Double] = (for (j <- 0 to nFeatures) yield if (i == j) eq else weight(j)).toList
          likelihood(eqB, width)
        }
      ))).toList
    val valsLikelihoodWidth = xEqui zip (xEqui.map(eq => likelihood(weight, eq)))
    val valsLikelihood = valsLikelihoodWeight ::: List(valsLikelihoodWidth)
    val namesLikelihood = names.map("Likelihood(" + _ + ")")
    Plotting.plotCurves(valsLikelihood, namesLikelihood, xlabel= "Value", name= "plots/reg_Bayes_likelihood_dep.pdf")
  }

  def _predict(X: List[List[Double]]): List[Double] =
    for (instance <- X) yield Maths.dot(weight, 1 :: instance)

  def predict(X: List[List[Double]]): List[Double] =
    _predict(DataTrafo.addPolyFeatures(X, degree))

  def train(X: List[List[Double]], y: List[Double]): Unit =
    _train(DataTrafo.addPolyFeatures(X, degree), y)

}
