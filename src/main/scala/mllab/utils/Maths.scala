package utils


/** Provides auxiliary functions for mathematical operations*/
object Maths{

  /** Dot product using lists as vectors*/
  def dot(a: List[Double], b: List[Double]): Double = {
    require(a.length == b.length, "both arguments must have the same length")
    (a zip b).map{case (x, y) => x * y}.sum
  }

  /** Vector addition using lists as vectors*/
  def plus(a: List[Double], b: List[Double]): List[Double] = {
    require(a.length == b.length, "both arguments must have the same length")
    (a zip b).map{case (x, y) => x + y}
  }

  /** Vector subtraction using lists as vectors*/
  def minus(a: List[Double], b: List[Double]): List[Double] =
    plus(a, b.map(-_))

  /** Vector L2 norm using list as vector */
  def abs(a: List[Double]): Double =
    Math.sqrt(dot(a, a))

  /** Euclidian distance between two vectors */
  def distance(a: List[Double], b: List[Double]): Double =
    abs(minus(a, b))

  /** Rounds to specified amount of digits */
  def round(a: Double, digits: Int): Double =
    scala.math.BigDecimal(a).setScale(digits, scala.math.BigDecimal.RoundingMode.HALF_UP).toDouble

  /** The factorial function x! */
  def factorial(x: Int): Int =
    if (x <= 1) 1
    else x * factorial(x-1)

  /** Normal (Gaussian) function
   * @param x Abscissa
   * @param m Mean
   * @param s Sigma
   */
  def norm(x: Double, m: Double, s: Double): Double =
    Math.exp( -Math.pow((x - m), 2) / (2 * Math.pow(s, 2))) / Math.sqrt(2 * math.Pi * Math.pow(s, 2))

  /** Logistic function */
  def logistic(x: Double): Double = 1.0 / (Math.exp(-x) + 1.0)

  /** Triangular function
  * @param x Abscissa
   * @param m Peak position
   * @param w Half width at y = 0
   */
  def triangular(x: Double, m: Double, w: Double): Double = {
    val dist = x - m
    val h = 0.5 / w
    val slope = h / w
    Math.max(0, h - Math.signum(dist) * dist * slope)
  }

  def rectangular(x: Double, f: Double, t: Double): Double =
    if (x <= f) 0.0
    else if (x >= t) 0.0
    else 1.0 / (t - f)

  /** Returns the mean of a list of values */
  def mean(l: List[Double]): Double =
    l.sum / l.length

    /** Returns the variance of a list of values */
  def variance(l: List[Double]): Double =
      l.map(a => Math.pow(a - mean(l), 2)).sum / l.size

  /** Returns the standard deviation of a list of values */
  def std(l: List[Double]): Double =
    Math.sqrt(variance(l))

  /** Returns the bernoulli probabilities for an event given a probability for 0 */
  def bernoulli(x: Int, p: Double): Double =
    Math.pow(p, x) * Math.pow((1 - p), 1-x)

  /** Returns the probability to observe a given histogram
   * @param x Histogram of counts
   * @param p Binned probability distribution
   */
  def multinomial(x: List[Int], p: List[Double]): Double = {
    require (round(p.sum, 9) == 1, "probabilities do not add up to unity: " + p.sum + " != 1")
    val num: Double = factorial(x.sum)
    val denom: Double = x.map(factorial(_)).product
    val prod: Double = (p zip x).map(px => Math.pow(px._1, px._2)).product
    num / denom * prod
  }

  /** Shannon entropy
   * @param pc Ratio of class instances
   */
  def entropy(pc: List[Double]): Double =
    - pc.filter(_ != 0).map(p => p * Math.log(p)).sum

  /** Gini impurity
   * @param pc Ratio of class instances
   */
  def gini(pc: List[Double]): Double =
    1 - pc.map(Math.pow(_, 2)).sum

}
