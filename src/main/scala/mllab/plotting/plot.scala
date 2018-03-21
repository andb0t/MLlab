package plotting

import classifiers._

import breeze.linalg._
import breeze.plot._


/** Provides functions for plotting data and algorithm results */
object Plotting {

  /** Plot labeled data
   *@param data List of features
   *@param labels List of labels
   *@param name Path to save the plot
   */
  def plotData(data: List[List[Double]], labels: List[Int], name: String="plots/plot.pdf"): Unit = {
    val f = Figure()
    f.visible= false
    val p = f.subplot(0)
    for (category: Int <- labels.toSet.toList.sorted) {
      val filteredData: List[List[Double]] = (data zip labels).filter(_._2 == category).map(_._1)
      val x: List[Double] = filteredData.map(e => e.head)
      val y: List[Double] = filteredData.map(e => e(1))
      p += plot(x, y, '.', name= "feature " + category)
    }
    p.xlabel = "Feature 0"
    p.ylabel = "Feature 1"
    p.legend = true
    p.title = "Data"
    f.saveas(name)
  }

  /** Plot labeled data and classifier decision
   *@param data List of features
   *@param labels List of labels
   *@param clf Trained classifier
   *@param name Path to save the plot
   */
  def plotClf(data: List[List[Double]], labels: List[Int], clf: Classifier, name: String="plots/clf.pdf"): Unit = {
    val predictions = clf.predict(data)
    val f = Figure()
    f.visible= false
    val p = f.subplot(0)
    // now plot all datapoints
    for (category: Int <- predictions.toSet.toList.sorted) {
      val filteredData: List[List[Double]] = (data zip predictions).filter(_._2 == category).map(_._1)
      val x: List[Double] = filteredData.map(e => e.head)
      val y: List[Double] = filteredData.map(e => e(1))
      p += plot(x, y, '.', name= "prediction " + category)
    }

    val wrong: List[Boolean] = (predictions zip labels).map{case (x, y) => x != y}
    val filteredData: List[List[Double]] = (data zip wrong).filter(_._2).map(_._1)
    val x: List[Double] = filteredData.map(e => e.head)
    val y: List[Double] = filteredData.map(e => e(1))
    p += plot(x, y, '+', colorcode= "r", name= "false prediction")

    p.xlabel = "feature 0"
    p.ylabel = "feature 1"
    p.title = clf.name + " decisions"
    p.legend = true
    f.saveas(name)

  }

  /** Plot classifier decision areas on an envelope plane of the data
   *@param data List of features
   *@param clf Trained classifier
   *@param name Path to save the plot
   */
  def plotGrid(data: List[List[Double]], clf: Classifier, name: String="plots/grid.pdf"): Unit = {

    def createGrid(xMin: Double, xMax: Double, yMin: Double, yMax: Double): List[List[Double]] = {
      val granularity: Int = 100
      val xVec: DenseVector[Double] = tile(linspace(xMin, xMax, granularity), granularity)
      val yLinSpace = linspace(yMin, yMax, granularity)
      val yVec: DenseVector[Double] =
        DenseVector.tabulate(Math.pow(granularity, 2).toInt){
          i => yLinSpace(i / granularity)
        }
      val xList = (for (i <- 0 until xVec.size) yield xVec(i)).toList
      val yList = (for (i <- 0 until yVec.size) yield yVec(i)).toList
      (xList zip yList).map(x => List(x._1, x._2))
    }
    val xMin = data.map(_.head).min
    val xMax = data.map(_.head).max
    val yMin = data.map(_(1)).min
    val yMax = data.map(_(1)).max
    val gridData = createGrid(xMin, xMax, yMin, yMax)
    val predictions = clf.predict(gridData)

    val f = Figure()
    f.visible= false
    val p = f.subplot(0)
    for (category: Int <- predictions.toSet.toList.sorted) {
      val filteredData: List[List[Double]] = (gridData zip predictions).filter(_._2 == category).map(_._1)
      val x: List[Double] = filteredData.map(e => e.head)
      val y: List[Double] = filteredData.map(e => e(1))
      p += plot(x, y, '.', name= "feature " + category)
    }
    p.xlabel = "feature 0"
    p.ylabel = "feature 1"
    p.title = clf.name + " decision map"
    p.legend = true
    f.saveas(name)
  }

  /** Plot a set of curves
   *@param curves List of curves, with a curve being a list of points (x, y)
   *@param names List of the curves' names
   *@param name Path to save the plot
   */
  def plotCurves(curves: List[List[(Double, Double)]], names: List[String]=Nil, name: String = "plots/curves.pdf"): Unit = {
    val f = Figure()
    f.visible= false
    val p = f.subplot(0)
    val x = linspace(0.0,1.0)
    for (i <- 0 until curves.length){
      val curve = curves(i)
      if (i < names.length) p += plot(curve.map(_._1), curve.map(_._2), name=names(i))
      else p += plot(curve.map(_._1), curve.map(_._2))
    }
    p.xlabel = "training epoch"
    if (curves.length == 1) p.ylabel = names.head
    p.legend = curves.length != 1
    f.saveas(name)
  }

  /** Plot data for regression
   *@param data List of features, e.g. in 2D: x-coordinates
   *@param labels List of labels, e.g. in 2D: y-coordinates
   *@param name Path to save the plot
   */
  def plotRegData(data: List[List[Double]], labels: List[Double], name: String="plots/plot.pdf"): Unit = {
    val f = Figure()
    f.visible= false
    val p = f.subplot(0)

    val dataPerFeature = data.transpose

    for (i <- 0 until dataPerFeature.length){
      val x = dataPerFeature(i)
      if (data.head.length == 1) p += plot(x, labels, '.')
      else p += plot(x, labels, '.', name= "feature " + i)
    }

    p.ylabel = "Label"
    if (data.head.length == 1) p.xlabel = "feature 0"
    p.legend = (data.head.length != 1)
    p.title = "Data"

    f.saveas(name)
  }
}
