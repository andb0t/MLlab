package mllab


object Maths{

  def dot(a: List[Double], b: List[Double]): Double = {
    require(a.length == b.length, "both arguments must have the same length")
    var prod: Double = 0
    for (i <- 0 until a.length) {
      prod += a(i) * b(i)
    }
    prod
  }

  def abs(a: List[Double]): Double = {
    Math.sqrt(dot(a, a))
  }

}
