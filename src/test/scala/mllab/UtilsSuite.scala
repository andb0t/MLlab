import org.scalatest._

import utils._


class UtilsSuite extends FunSuite {

  test ("dot product") {
    assert (Maths.dot(List(1, 2, -1), List(0, -1, -1)) === -1)
    assert (Maths.dot(List(0, 3), List(1, -1)) === -3)
  }

  test ("vector addition") {
    assert (Maths.plus(List(1, 2, -1), List(0, -1, -1)) === List(1, 1, -2))
    assert (Maths.plus(List(0, 3), List(1, -1)) === List(1, 2))
  }

  test ("absolute value") {
    assert (Maths.abs(List(1, 2, -1)) === Math.sqrt(6))
    assert (Maths.abs(List(0, 0)) === 0)
  }

  test ("factorial") {
    assert (Maths.factorial(0) === 1)
    assert (Maths.factorial(1) === 1)
    assert (Maths.factorial(2) === 2)
    assert (Maths.factorial(4) === 24)
  }

  test ("rounding") {
    assert (Maths.round(0.145, 2) === 0.15)
    assert (Maths.round(0.145, 0) === 0)
    assert (Maths.round(1.545, 0) === 2)
    assert (Maths.round(1.55, 1) === 1.6)
  }

  test ("normal distribution") {
    // scipy.stats.norm.pdf(0, 0, 1)
    assert (Maths.norm(0, 0, 1) == 0.3989422804014327)
    assert (Maths.norm(1, 1, 1) == 0.3989422804014327)
    assert (Maths.norm(2, -1, 4) == 0.07528435803870111)
  }

  test ("triangular distribution") {
    assert (Maths.triangular(0, 0, 1) === 0.5)
    assert (Maths.triangular(0.5, 0, 1) === 0.25)
    assert (Maths.triangular(0.5, 1, 1) === 0.25)
    assert (Maths.triangular(100, 1, 1) === 0)
  }

  test ("bernoulli distribution") {
    assert (Maths.bernoulli(0, 0.5) == 0.5)
    assert (Maths.bernoulli(1, 0.5) == 0.5)
    assert (Maths.bernoulli(1, 0.3) == 0.3)
    assert (Maths.bernoulli(0, 0.3) == 0.7)
  }

  test ("multinomial distribution") {
    assert (Maths.round(Maths.multinomial(List(1, 2, 3), List(0.2, 0.3, 0.5)), 3) === 0.135)
    assert (Maths.multinomial(List(1, 0), List(1, 0)) === 1.0)
    assert (Maths.multinomial(List(1, 0), List(0, 1)) === 0)
  }

  test ("mean") {
    // numpy.mean([1, 2, 3])
    assert (Maths.mean(List(1, 2, 3)) === 2)
    assert (Maths.mean(List(1.3, 3.7, 123)) === 42.666666666666664)
  }

  test ("variance") {
    // numpy.var([1, 2, 3])
    assert (Maths.variance(List(1, 2, 3)) === 0.6666666666666666)
    assert (Maths.variance(List(1.3, 3.7, 123)) === 3227.682222222223)
  }

  test ("standard deviation") {
    // numpy.std([1, 2, 3])
    assert (Maths.std(List(1, 2, 3)) === 0.816496580927726)
    assert (Maths.std(List(1.3, 3.7, 123)) === 56.81269419964365)
  }

}
