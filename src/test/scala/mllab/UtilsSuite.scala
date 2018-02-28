import org.scalatest._

import utils._


class UtilsSuite extends FunSuite {

  test("dot product"){
    assert (Maths.dot(List(1, 2, -1), List(0, -1, -1)) === -1)
    assert (Maths.dot(List(0, 3), List(1, -1)) === -3)
  }

  test("vector addition"){
    assert (Maths.plus(List(1, 2, -1), List(0, -1, -1)) === List(1, 1, -2))
    assert (Maths.plus(List(0, 3), List(1, -1)) === List(1, 2))
  }

  test("matrix addition"){
    val m0: List[List[Double]] = List(List(1, 2, 3), List(4, 5, 6))
    val m1: List[List[Double]] = List(List(7, 8, 9), List(-3, -2, -1))
    val m2: List[List[Double]] = List(List(8, 10, 12), List(1, 3, 5))
    assert (Maths.plusM(m0, m1) === m2)
  }

  test("vector scaling"){
    assert (Maths.times(3, List(1, 2, -1)) === List(3, 6, -3))
  }

  test("matrix scaling"){
    val m0: List[List[Double]] = List(List(1, 2, 3), List(4, -5, 6))
    val m1: List[List[Double]] = List(List(3, 6, 9), List(12, -15, 18))
    assert (Maths.timesM(3, m0) === m1)
  }

  test("matrix vector multiplication"){
    val m0: List[List[Double]] = List(List(1, 2, 3), List(4, -5, 6))
    assert (Maths.timesMV(m0, List(1, 2, -1)) === List(2, -12))
  }

  test("vector matrix multiplication"){
    val m0: List[List[Double]] = List(List(1, 2), List(-3, 4), List(-5, 6))
    assert (Maths.timesVM(List(1, 2, -1), m0) === List(0, 4))
  }

  test("matrix multiplication"){
    val m0: List[List[Double]] = List(List(1, 2, 3), List(4, -5, 6))
    val m1: List[List[Double]] = List(List(2, 1), List(2, -1), List(1, 0))
    val m2: List[List[Double]] = List(List(9, -1), List(4, 9))
    assert (Maths.timesMM(m0, m1) === m2)
  }

  test("absolute value"){
    assert (Maths.abs(List(1, 2, -1)) === Math.sqrt(6))
    assert (Maths.abs(List(0, 0)) === 0)
  }

}
