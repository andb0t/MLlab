package regressors

import scala.collection.mutable.ListBuffer

import evaluation._
import utils._


class LinearRegressor(alpha: Double = 1, tol: Double = 0.001, maxIter: Int = 1000, degree: Int=3) extends Regressor {

  val name: String = "LinearRegressor"

  var weight = new ListBuffer[Double]()
  var bias: Double = 0

  var lossEvolution = new ListBuffer[(Double, Double)]()

  /** Add polynomial features to instances
   * @param degree Maximum order of polynomial features to add
   * @return Original instance extended by polynomial combination of its features
   */
  def addPolyFeatures(X: List[List[Double]], degree: Int): List[List[Double]] = {
    if (degree == 1) X
    else {
      val xFeatures = X.transpose
      val nFeatures = xFeatures.length

      def polyMap(deg: Int): List[Map[Int, Int]] = {
        // val maps = List(Map(0 -> 2), Map(0 -> 3))
        val maps = for (i <- 2 to degree) yield Map(0 -> i)
        maps.toSet.toList
      }

      def addFeatures(xExtended: List[List[Double]], featureMapList: List[Map[Int, Int]]): List[List[Double]] = featureMapList match {
        case Nil => xExtended
        case mHead::mTail => {
          val indices: List[Int] = (for (m <- mHead) yield List.fill(m._2)(m._1)).flatten.toList
          val newFeature: List[Double] = for (x <- X) yield indices.map(i => x(i)).product
          addFeatures(newFeature::xExtended, mTail)
        }
      }

      val xExtended = addFeatures(xFeatures, polyMap(degree)).transpose
      println("Instances with the transformed features:")
      println(X.head + " -> " + xExtended.head)
      println(X(1) + " -> " + xExtended(1))
      println(X(2) + " -> " + xExtended(2))
      println(X(3) + " -> " + xExtended(3))
      xExtended
    }
  }

  def lossGradient(X: List[List[Double]], y: List[Double]): List[Double] = {
    // dLoss = d(MSE scaled) = Sum (const * linearDistanceScaled * instanceVector)
    val linDist: List[Double] = for (xy <- X zip y) yield (Maths.dot(weight.toList, xy._1) + bias - xy._2) / y.length
    val weightGradient: List[Double] = (X zip linDist).map{case (x, d) => x.map(_ * d)}.reduce(Maths.plus(_, _))
    val biasGradient: Double = linDist.sum
    biasGradient::weightGradient
  }

  def _train(X: List[List[Double]], y: List[Double]): Unit = {
    require(X.length == y.length, "both arguments must have the same length")
    for (i <- 0 until X.head.length)
      weight += 0
    bias = 0

    def updateWeights(count: Int): Unit = {
      val loss = Evaluation.MSE(_predict(X), y)
      lossEvolution += Tuple2(count.toDouble, loss)
      if (loss > tol && count < maxIter) {
        val weightUpdate = lossGradient(X, y).map(_ * alpha)
        if (count % 100 == 0 || (count < 50 && count % 10 == 0) || (count < 5))
          println(s"Step $count with loss %.4e".format(loss))
        // println(s"$count. Step with loss: %.3f".format(loss))
        // println(" - current MSE: " + loss)
        // println(" - current weight " + weight + " bias " + bias)
        // println(" - weightUpdate " + weightUpdate)
        bias = bias - weightUpdate.head
        for (i <- 0 until weight.length)
          weight(i) = weight(i) - weightUpdate(i + 1)
        updateWeights(count + 1)
      } else{
        println(s"Final values after $count steps at loss %.3f:".format(loss))
        println("weight: " + weight + " bias: " + bias)
      }
    }

    updateWeights(0)
  }

  def _predict(X: List[List[Double]]): List[Double] =
    for (instance <- X) yield Maths.dot(weight.toList, instance) + bias

  def predict(X: List[List[Double]]): List[Double] =
    _predict(addPolyFeatures(X, degree))

  def train(X: List[List[Double]], y: List[Double]): Unit =
    _train(addPolyFeatures(X, degree), y)

  override def diagnostics(): Map[String, List[(Double, Double)]] = {
    Map("loss" -> lossEvolution.toList)
  }
}
