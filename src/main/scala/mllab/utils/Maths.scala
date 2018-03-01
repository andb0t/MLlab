package utils


object Maths{

  def dot(a: List[Double], b: List[Double]): Double = {
    require(a.length == b.length, "both arguments must have the same length")
    (a zip b).map{case (x, y) => x * y}.sum
  }

  def plus(a: List[Double], b: List[Double]): List[Double] = {
    require(a.length == b.length, "both arguments must have the same length")
    (a zip b).map{case (x, y) => x + y}
  }

  def plusM(a: List[List[Double]], b: List[List[Double]]): List[List[Double]] = {
    require(a.length == b.length, "both arguments must have the same length")
    (a zip b).map{case (x, y) => plus(x, y)}
  }

  def times(a: Double, b: List[Double]): List[Double] = {
    b.map(x => a * x)
  }

  def timesM(a: Double, b: List[List[Double]]): List[List[Double]] = {
    b.map(x => times(a, x))
  }

  def timesMM(a: List[List[Double]], b: List[List[Double]]): List[List[Double]] = {
    require(a.head.length == b.length, "matrix(" + a.length + ", " + a.head.length +") and vector(" + b.length + ") dimensions do not fit!")
    (for (bm <- b.transpose) yield a.map(x => dot(x, bm))).transpose
  }

  def timesMV(a: List[List[Double]], b: List[Double]): List[Double] = {
    require(a.head.length == b.length, "matrix(" + a.length + ", " + a.head.length +") and vector(" + b.length + ") dimensions do not fit!")
    a.map(x => dot(x, b))
  }

  def timesVM(a: List[Double], b: List[List[Double]]): List[Double] = {
    require(a.length == b.length, "vector(" + a.length +") and matrix(" + b.length + ", " + b.head.length + ") dimensions do not fit!")
    timesMM(List(a), b).flatten
  }

  def squareM(M: List[List[Double]]): List[List[Double]] =
    for (v <- M) yield for (e <- v) yield Math.pow(e, 2)

  def hadamard(a: List[Double], b: List[Double]): List[Double] = {
    require(a.length == b.length, "both arguments must have the same length")
    (a zip b).map{case (x, y) => x * y}
  }

  def hadamardMM(a: List[List[Double]], b: List[List[Double]]): List[List[Double]] = {
    require(a.length == b.length && a.head.length == b.head.length,
      "matrix(" + a.length + ", " + a.head.length +") and " +
      "matrix(" + b.length + ", " + b.head.length + ") dimensions do not fit!")
    (a zip b).map{case (x, y) => hadamard(x, y)}
  }

  def abs(a: List[Double]): Double = {
    Math.sqrt(dot(a, a))
  }

}
