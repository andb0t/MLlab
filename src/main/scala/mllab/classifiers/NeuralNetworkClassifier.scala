package classifiers

import scala.collection.mutable.ListBuffer

import utils._


class NeuralNetworkClassifier() extends Classifier {

  type Matrix2D = List[List[Double]]
  type Vector = List[Double]

  var W = new ListBuffer[Matrix2D]()
  var b = new ListBuffer[Vector]()

  val inputLayer: Int = 2
  val middleLayer: Int = 10
  val outputLayer: Int = 2  // == 2 required for this implementation of binary classification

  def train(X: List[List[Double]], y: List[Int]): Unit =
    require(X.length == y.length, "both arguments must have the same length")

    def initVal: Double = Math.random
    // def initVal: Double = 1.0

    val inputW = List.fill(inputLayer)(List.fill(middleLayer)(initVal))
    val middleW = List.fill(middleLayer)(List.fill(outputLayer)(initVal))
    List(inputW, middleW).copyToBuffer(W)

    val inputb = List.fill(middleLayer)(initVal)
    val middleb = List.fill(outputLayer)(initVal)
    List(inputb, middleb).copyToBuffer(b)

    // println("Initial weights:")
    // println(W.mkString("\n"))
    // println("Initial biases:")
    // println(b.mkString("\n"))




  def predict(X: List[List[Double]]): List[Int] = {

    def neuronTrafo(X: List[List[Double]], W: Matrix2D, b: Vector): List[Vector] =
      for (x <- X) yield Maths.plus((x zip W) map{case (f, w) => w.map(_ * f)} reduce (Maths.plus(_, _)), b)

    def activate(Z: List[Vector]): List[Vector] =
       Z.map(x => x.map(Math.tanh))

    def probabilities(Z: List[Vector]): List[Vector] =
      Z.map(z => z.map(Math.exp)).map(z => z.map(_ / z.sum))

    def mostProbable(P: List[Vector]): List[Int] =
      P.map(_.zipWithIndex.maxBy(_._1)._2)

    val inputZ = neuronTrafo(X, W(0), b(0))
    // println(inputZ)
    val inputZactive = activate(inputZ)
    // println(inputZactive)
    val middleZ = neuronTrafo(inputZactive, W(1), b(1))
    // println(middleZ)
    val probs = probabilities(middleZ)
    // println(probs)
    val prediction = mostProbable(probs)
    // println(prediction)

    prediction
  }

}
