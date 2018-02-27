import org.scalatest._

import evaluation._


class EvaluationSuite extends FunSuite {

  test("precision"){
    assert (Evaluation.precision(List(1, 1), List(1, 0)) === 0.5)
    assert (Evaluation.precision(List(1, 1, 1, 0, 0, 1, 0, 1), List(1, 0, 1, 0, 1, 1, 0, 0)) === 0.6)
  }

  test("recall"){
    assert (Evaluation.recall(List(1, 0), List(1, 1)) === 0.5)
    assert (Evaluation.recall(List(1, 1), List(1, 0)) === 1)
    assert (Evaluation.recall(List(1, 1, 1, 0, 0, 1, 0, 1), List(1, 0, 1, 0, 1, 1, 0, 0)) === 0.75)
  }

  test("f1"){
    assert (Evaluation.f1(List(1, 1), List(1, 0)) === Math.sqrt(0.5))
    assert (Evaluation.f1(List(1, 1, 1, 0, 0, 1, 0, 1), List(1, 0, 1, 0, 1, 1, 0, 0)) === Math.sqrt(0.6*0.75))
  }

  test("mean squared error"){
    assert (Evaluation.MSE(List(0, 0), List(1, 2)) === 2.5)
    assert (Evaluation.MSE(List(1, 4), List(-2, 3)) === 5)
  }

}
