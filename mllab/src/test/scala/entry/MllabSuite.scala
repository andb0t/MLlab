package entry

import org.scalatest._

import mllab.Evaluation


class MllabSuite extends FunSuite {
  test("one equals one"){
    assert (1 === 1)
  }

  test("precision test"){
    assert (Evaluation.precision(List(1, 1), List(1, 0)) === 0.5)
  }

  test("recall test"){
    assert (Evaluation.recall(List(1, 0), List(1, 1)) === 0.5)
  }

  test("f1 test"){
    assert (Evaluation.precision(List(1, 1), List(1, 0)) === 0.5)
  }
}
