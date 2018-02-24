package utils


object Maths{

  def dot(a: List[Double], b: List[Double]): Double = {
    require(a.length == b.length, "both arguments must have the same length")
    (a zip b).map{case (x, y) => x * y}.sum
  }

  def abs(a: List[Double]): Double = {
    Math.sqrt(dot(a, a))
  }

}
