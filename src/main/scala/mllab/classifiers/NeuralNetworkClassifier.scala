package classifiers

import scala.collection.mutable.ListBuffer

import utils._


class NeuralNetworkClassifier(alpha: Double = 0.01, regularization: Double = 0.01) extends Classifier {

  type Matrix = List[List[Double]]
  type Vector = List[Double]

  var W = new ListBuffer[Matrix]()
  var b = new ListBuffer[Vector]()

  val inputLayer: Int = 2
  val middleLayer: Int = 10
  val outputLayer: Int = 2  // == 2 required for this implementation of binary classification



  def neuronTrafo(X: List[List[Double]], W: Matrix, b: Vector): List[Vector] =
    for (x <- X) yield Maths.plus(Maths.timesVM(x, W), b)

  def activate(Z: List[Vector]): List[Vector] =
    Z.map(z => z.map(Math.tanh))

  def probabilities(Z: List[Vector]): List[Vector] =
    Z.map(z => z.map(Math.exp)).map(z => z.map(_ / z.sum))

  def getProbabilities(X: List[List[Double]]): List[Vector] = {

    val inputZ = neuronTrafo(X, W(0), b(0))
    // println(inputZ)
    val inputZactive = activate(inputZ)
    // println(inputZactive)
    val middleZ = neuronTrafo(inputZactive, W(1), b(1))
    // println(middleZ)
    val probs = probabilities(middleZ)
    // println(probs)
    probs
  }

  def getLoss(X: List[List[Double]], y: List[Int]): Double = {
    val probs = getProbabilities(X)
    val correctLogProbs: List[Vector] = (probs zip y).
      map{case (v, i) => for (vs <- v if (vs == v(i))) yield  - Math.log(vs)}
    val dataLoss = correctLogProbs.flatten.sum
    val dataLossReg = dataLoss + regularization / 2 * W.map(w => Maths.squareM(w).flatten.sum).sum
    val loss: Double = dataLossReg / X.length
    loss
  }

  def train(X: List[List[Double]], y: List[Int]): Unit = {
    require(X.length == y.length, "both arguments must have the same length")

    println("Initialize weigths and biases")
    val rnd = new scala.util.Random(1337)
    def initVal: Double  = rnd.nextFloat

    val inputW = List.fill(inputLayer)(List.fill(middleLayer)(initVal / Math.sqrt(inputLayer)))  // (2, 10)
    val middleW = List.fill(middleLayer)(List.fill(outputLayer)(initVal / Math.sqrt(middleLayer)))  // (10, 2)
    List(inputW, middleW).copyToBuffer(W)

    val inputb = List.fill(middleLayer)(0.0)  // (10)
    val middleb = List.fill(outputLayer)(0.0)  // (2)
    List(inputb, middleb).copyToBuffer(b)

    // println("Initial weights:")
    // println(W.mkString("\n"))
    // println("Initial biases:")
    // println(b.mkString("\n"))

    println("Apply backpropagation gradient descent")
    val maxEpoch: Int = 1000

    def gradientDescent(count: Int): Unit = {
      // a simple implementation: http://www.wildml.com/2015/09/implementing-a-neural-network-from-scratch/
      // check http://neuralnetworksanddeeplearning.com/chap2.html
      if (count < maxEpoch) {

        if (count % 100 == 0) {
          val loss = getLoss(X, y)
          println(s"- epoch $count: loss $loss")
          // println("Weights:")
          // println(W.mkString("\n"))
          // println("Biases:")
          // println(b.mkString("\n"))
        }

        // forward propagation
        val activationLayer = activate(neuronTrafo(X, W(0), b(0)))  // (nInstances, 10)
        // println("Instances: " + activationLayer.length + ",  new features: " + activationLayer.head.length)
        val probs = getProbabilities(X)
        // backward propagation
        val outputDelta: List[Vector] = (probs zip y).
          map{case (v, i) => for (vs <- v) yield if (vs == v(i)) vs - 1 else vs}  // (nInstances, 2)
        val dW1: Matrix = Maths.timesMM(activationLayer.transpose, outputDelta)  // (10, 2)
        val db1: Vector = outputDelta.transpose.map(_.sum)  // (2)
        // delta2 = outputDelta.dot(W(1).T) * (1 - np.power(activationLayer, 2))
        val partialDerivWeight = Maths.timesMM(outputDelta, W(1).transpose)  // (nInstances, 10)
        val particalDerivActiv = activationLayer.map(al => al.map(ae => 1 - Math.pow(ae, 2)))  // (nInstances, 10)
        val middleDelta: List[Vector] = Maths.hadamardMM(partialDerivWeight, particalDerivActiv)  // (nInstances, 10)
        val dW0: Matrix = Maths.timesMM(X.transpose, middleDelta)  // (2, 10)
        val db0: Vector = middleDelta.transpose.map(_.sum)  // (10)
        // regularization
        val dW1reg: Matrix = Maths.plusM(dW1, Maths.timesM(regularization, W(1)))
        val dW0reg: Matrix = Maths.plusM(dW0, Maths.timesM(regularization, W(0)))
        // updates
        val newW1: Matrix = Maths.plusM(W(1), Maths.timesM(-alpha, dW1reg))
        val newW0: Matrix = Maths.plusM(W(0), Maths.timesM(-alpha, dW0reg))
        val newb1: Vector = Maths.plus(b(1), Maths.times(-alpha, db1))
        val newb0: Vector = Maths.plus(b(0), Maths.times(-alpha, db0))
        // updates weights
        W.clear()
        b.clear()
        List(newW0, newW1).copyToBuffer(W)
        List(newb0, newb1).copyToBuffer(b)

        gradientDescent(count + 1)
      }else println(s"Training finished after $count epochs!")
    }

    gradientDescent(0)

  }


  def predict(X: List[List[Double]]): List[Int] = {

    def mostProbable(P: List[Vector]): List[Int] =
      P.map(_.zipWithIndex.maxBy(_._1)._2)

    val probs = getProbabilities(X)
    val prediction = mostProbable(probs)
    // println(prediction)
    prediction
  }

}
