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

  /** Vector L2 norm using list as vector */
  def abs(a: List[Double]): Double = {
    Math.sqrt(dot(a, a))
  }

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
    require (p.sum == 1, "probabilities do not add up to unity")
    val num: Double = factorial(x.sum)
    val denom: Double = x.map(factorial(_)).product
    val prod: Double = (p zip x).map(px => Math.pow(px._1, px._2)).product
    num / denom * prod
  }
}
