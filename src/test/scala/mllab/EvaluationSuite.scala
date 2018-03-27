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

  test("log loss"){
    assert (Evaluation.LogLoss(List(0.8, 0.4, 0.5), List(1, 0, 1)) === 0.47570545188004854)
    assert (Evaluation.LogLoss(List(0.01, 0, 1), List(1, 0, 1)) === 1.5350567286626975)
  }

  test("mean squared error"){
    assert (Evaluation.MSE(List(0, 0), List(1, 2)) === 2.5)
    assert (Evaluation.MSE(List(1, 4), List(-2, 3)) === 5)
    assert (Evaluation.MSE(List(2.5, 0.0, 2, 8), List(3, -0.5, 2, 7)) === 0.375)
  }

  test ("mean absolute error") {
    assert (Evaluation.MAE(List(2.5, 0.0, 2, 8), List(3, -0.5, 2, 7)) === 0.5)
  }

  test ("median absolute error") {
    assert (Evaluation.MedAE(List(2.5, 0.0, 2, 8), List(3, -0.5, 2, 7)) === 0.5)
  }

  test ("r sqared") {
    assert (Evaluation.RSqared(List(2.5, 0.0, 2, 8), List(3, -0.5, 2, 7)) === 0.9486081370449679)
  }

  test ("explained variance score") {
    assert (Evaluation.explainedVariance(List(2.5, 0.0, 2, 8), List(3, -0.5, 2, 7)) === 0.9571734475374732)
  }

  test ("mean squared logarithmic error") {
    assert (Evaluation.MSLE(List(2.5, 5, 4, 8), List(3, 5, 2.5, 7)) === 0.03973012298459379)
  }

}
