package mllab

import org.apache.spark.sql.SparkSession
import com.databricks.spark.csv._


object Mllab {
  def main(args: Array[String]): Unit = {
    println("Execute MLlab!")


    println("Initialize spark session")

    val spark = SparkSession
      .builder()
      .appName("mllab")
      .master("local")
      .getOrCreate()

    spark.sparkContext.setLogLevel("OFF")
    import spark.implicits._

    val df = spark
      .read
      .format("com.databricks.spark.csv")
      .option("header", true)
      .load("src/test/resources/train.csv")


    println{"Train the model"}
    val trainReader = new Reader("src/test/resources/train.csv", label=3, index=0)
    trainReader.loadFile()
    val X_train = trainReader.getX()
    val y_train = trainReader.getY()
    // val clf = new RandomClassifier()
    // val clf = new kNNClassifier(k=3)
    val clf = new DecisionTreeClassifier(depth=2)
    clf.train(X_train, y_train)
    // println("Check prediction on training set")
    // clf.predict(X_train)

    println{"Apply to test set"}
    val testReader = new Reader("src/test/resources/test.csv", label=3, index=0)
    testReader.loadFile()
    val X_test = testReader.getX()
    val y_test = testReader.getY()
    println("Now do prediction on test set")
    val y_pred = clf.predict(X_test)
    assert (y_pred.length == y_test.length)
    println("Predicted values:")
    // for (i <- 0 until Math.min(y_pred.length, 10)) {
    //   println("Test instance " + i + ": prediction " + y_pred(i) + " true value " + y_test(i))
    // }

    println("Evaulate of the model")
    Evaluation.matrix(y_pred, y_test)
    println("Precision: %.2f".format(Evaluation.precision(y_pred, y_test)))
    println("Recall: %.2f".format(Evaluation.recall(y_pred, y_test)))
    println("f1: %.2f".format(Evaluation.f1(y_pred, y_test)))

    println("Visualize the data")
    trainReader.plot()

    // spark.stop()
  }
}
