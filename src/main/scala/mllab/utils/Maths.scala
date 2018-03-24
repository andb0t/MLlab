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

  def bernoulli(x: Int, p: Double): Double =
    Math.pow(p, x) * Math.pow((1 - p), 1-x)

}
