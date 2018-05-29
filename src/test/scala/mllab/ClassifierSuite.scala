import org.scalatest._

import classifiers._
import utils._


class ClassifierSuite extends FunSuite {

  test ("kNN: updateNearest") {
    val oldNearest = List((5.0, 0), (3.0, 1), (7.0, 0))
    val newNearest = List((5.0, 0), (3.0, 1), (2.0, 1))
    val x = List(1.0, 3.1, 2.0, -3.3, 0.4)
    val instanceGood = List(1.0, 3.1, 2.0, -3.3, 2.4)
    val label = 1
    assert (new kNNClassifier().updateNearest(x, instanceGood, label, oldNearest) === newNearest)
    val instanceBad = List(1.0, 3.1, 2.0, -3.3, 8.4)
    assert (new kNNClassifier().updateNearest(x, instanceBad, label, oldNearest) === oldNearest)
  }

  test ("DecisionTree: getPurity") {
    val featureX = List(1.0, 2.0, 3.0, 3.0, 2.5)
    val yThisNode = List(0, 0, 1, 1, 0)
    val weightsThisNode = List(1.0, 1.0, 1.0, 1.0, 1.0)
    def roundTupleOne(x: Tuple2[Double, Boolean]): Tuple2[Double, Boolean] = Tuple2(Maths.round(x._1, 9), x._2)
    assert (new DecisionTreeClassifier().getPurity(featureX, yThisNode, weightsThisNode, 2.7, "gini") === (0, true))
    assert (roundTupleOne(new DecisionTreeClassifier().getPurity(featureX, yThisNode, weightsThisNode, 2.2, "gini")) === (Maths.round(-4.0/15, 9), true))
    assert (new DecisionTreeClassifier().getPurity(featureX, yThisNode,weightsThisNode, 0, "gini") === (-0.48, true))
  }

}
