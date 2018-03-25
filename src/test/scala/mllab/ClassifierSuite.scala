import org.scalatest._

import classifiers._


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

}
