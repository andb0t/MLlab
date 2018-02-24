package mllab

import org.scalatest._

import utils._
import classifiers._


class MllabSuite extends FunSuite {

  test("precision"){
    assert (Evaluation.precision(List(1, 1), List(1, 0)) === 0.5)
  }

  test("recall"){
    assert (Evaluation.recall(List(1, 0), List(1, 1)) === 0.5)
  }

  test("f1"){
    assert (Evaluation.precision(List(1, 1), List(1, 0)) === 0.5)
  }

  test("dot product"){
    assert (Maths.dot(List(1, 2, -1), List(0, -1, -1)) === -1)
  }

  test("absolute value"){
    assert (Maths.abs(List(1, 2, -1)) === Math.sqrt(6))
  }

}
