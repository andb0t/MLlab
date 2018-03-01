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

  test("absolute value"){
    assert (Maths.abs(List(1, 2, -1)) === Math.sqrt(6))
    assert (Maths.abs(List(0, 0)) === 0)
  }

}
