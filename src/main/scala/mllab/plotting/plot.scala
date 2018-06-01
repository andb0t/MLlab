package plotting

import classifiers._
import clustering._
import regressors._
import utils._

import breeze.linalg._
import breeze.plot._


/** Provides functions for plotting data and algorithm results */
object Plotting {

  /** Plot labeled data
   *@param data List of features
   *@param labels List of labels
   *@param name Path to save the plot
   */
  def plotClfData(data: List[List[Double]], labels: List[Int], name: String="plots/data.pdf"): Unit = {
    val f = Figure()
    f.visible= false
    val p = f.subplot(0)
    for (category: Int <- labels.toSet.toList.sorted) {
      val filteredData: List[List[Double]] = (data zip labels).filter(_._2 == category).map(_._1)
      val x: List[Double] = filteredData.map(e => e.head)
      val y: List[Double] = filteredData.map(e => e(1))
      p += plot(x, y, '.', name= "Class " + category)
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
      p += plot(x, y, '.', name= "Prediction " + category)
    }

    val wrong: List[Boolean] = (predictions zip labels).map{case (x, y) => x != y}
    val filteredData: List[List[Double]] = (data zip wrong).filter(_._2).map(_._1)
    val x: List[Double] = filteredData.map(e => e.head)
    val y: List[Double] = filteredData.map(e => e(1))
    p += plot(x, y, '+', colorcode= "r", name= "False prediction")

    p.xlabel = "Feature 0"
    p.ylabel = "Feature 1"
    p.title = clf.name + " decisions"
    p.legend = true
    f.saveas(name)

  }

  /** Plot labeled data and classifier decision
   *@param data List of features
   *@param predictions List of predicted classes
   *@param clu Trained classifier
   *@param name Path to save the plot
   */
  def plotClu(data: List[List[Double]], predictions: List[Int], clu: Clustering, drawCentroids: Boolean=false, name: String="plots/clu.pdf"): Unit = {
    val f = Figure()
    f.visible= false
    val p = f.subplot(0)
    // now plot all datapoints
    val centroids = clu.clusterMeans()

    if (drawCentroids) {
      val finalCentroids: List[List[Double]] = centroids.map(_.last)
      val x: List[Double] = finalCentroids.map(e => e.head)
      val y: List[Double] = finalCentroids.map(e => e(1))
      p += plot(x, y, '+', colorcode= "r", name= "Cluster means")
    }

    for (i <- 0 until centroids.length) {
      val col: String = StringTrafo.convertToColorCode(PaintScale.Category10(i % 10))
      if (drawCentroids) {
        val xEvol: List[Double] = centroids(i).map(e => e.head)
        val yEvol: List[Double] = centroids(i).map(e => e(1))
        p += plot(xEvol, yEvol, '-', colorcode=col, name= " ")
      }
      val filteredData: List[List[Double]] = (data zip predictions).filter(_._2 == i).map(_._1)
      val x: List[Double] = filteredData.map(e => e.head)
      val y: List[Double] = filteredData.map(e => e(1))
      p += plot(x, y, '.', colorcode=col, name= "Cluster " + i)
    }

    p.xlabel = "Feature 0"
    p.ylabel = "Feature 1"
    p.title = clu.name + " results"
    if (centroids.length < 10) p.legend = true
    f.saveas(name)

  }

  /** Plot classifier decision areas on an envelope plane of the data
   *@param data List of features
   *@param clf Trained classifier
   *@param name Path to save the plot
   */
  def plotClfGrid(data: List[List[Double]], clf: Classifier, name: String="plots/grid.pdf"): Unit = {
    val xMin = data.map(_.head).min
    val xMax = data.map(_.head).max
    val yMin = data.map(_(1)).min
    val yMax = data.map(_(1)).max
    val gridData = Trafo.createGrid(xMin, xMax, yMin, yMax)
    val predictions = clf.predict(gridData)

    val f = Figure()
    f.visible= false
    val p = f.subplot(0)
    for (category: Int <- predictions.toSet.toList.sorted) {
      val filteredData: List[List[Double]] = (gridData zip predictions).filter(_._2 == category).map(_._1)
      val x: List[Double] = filteredData.map(e => e.head)
      val y: List[Double] = filteredData.map(e => e(1))
      p += plot(x, y, '.', name= "Prediction " + category)
    }
    p.xlabel = "Feature 0"
    p.ylabel = "Feature 1"
    p.title = clf.name + " decision map"
    p.legend = true
    f.saveas(name)
  }

  /** Plot clustering decision areas on an envelope plane of the data
   *@param data List of features
   *@param clu Trained clusterer
   *@param name Path to save the plot
   */
  def plotCluGrid(data: List[List[Double]], clu: Clustering, name: String="plots/grid.pdf"): Unit = {
    if (data.head.length == 2) {
      val xMin = data.map(_.head).min
      val xMax = data.map(_.head).max
      val yMin = data.map(_(1)).min
      val yMax = data.map(_(1)).max
      val gridData = Trafo.createGrid(xMin, xMax, yMin, yMax)
      val predictions = clu.predict(gridData)

      val f = Figure()
      f.visible= false
      val p = f.subplot(0)
      for (category: Int <- predictions.toSet.toList.sorted) {
        val filteredData: List[List[Double]] = (gridData zip predictions).filter(_._2 == category).map(_._1)
        val x: List[Double] = filteredData.map(e => e.head)
        val y: List[Double] = filteredData.map(e => e(1))
        p += plot(x, y, '.', name= "Cluster " + category)
      }
      p.xlabel = "Feature 0"
      p.ylabel = "Feature 1"
      p.title = clu.name + " cluster map"
      if (clu.clusterMeans.length < 10) p.legend = true
      f.saveas(name)
    }
  }

  /** Plot a set of curves
   *@param curves List of curves, with a curve being a list of points (x, y)
   *@param names List of the curves' names
   *@param name Path to save the plot
   */
  def plotCurves(curves: List[List[(Double, Double)]], names: List[String]=Nil, name: String = "plots/curves.pdf", xlabel: String="Training epoch", ylabel: String=""): Unit = {
    val f = Figure()
    f.visible= false
    val p = f.subplot(0)
    for (i <- 0 until curves.length){
      val curve = curves(i)
      if (i < names.length) p += plot(curve.map(_._1), curve.map(_._2), name=names(i))
      else p += plot(curve.map(_._1), curve.map(_._2))
    }
    p.xlabel = xlabel
    if (ylabel != "") p.ylabel = ylabel
    else if (curves.length == 1) p.ylabel = names.head
    p.legend = curves.length != 1
    f.saveas(name)
  }

  /** Plot data for regression
   *@param data List of features, e.g. in 2D: x-coordinates
   *@param labels List of labels, e.g. in 2D: y-coordinates
   *@param name Path to save the plot
   */
  def plotRegData(data: List[List[Double]], labels: List[Double], name: String="plots/data.pdf"): Unit = {
    val f = Figure()
    f.visible= false
    val p = f.subplot(0)

    val dataPerFeature = data.transpose

    for (i <- 0 until dataPerFeature.length){
      val x = dataPerFeature(i)
      if (data.head.length == 1) p += plot(x, labels, '.')
      else p += plot(x, labels, '.', name= "Regression " + i)
    }

    p.ylabel = "Label"
    if (data.head.length == 1) p.xlabel = "Feature 0"
    p.legend = (data.head.length != 1)
    p.title = "Data"

    f.saveas(name)
  }

  /** Plot data for regression
   *@param data List of features, e.g. in 2D: x-coordinates
   *@param labels List of labels, e.g. in 2D: y-coordinates
   *@param reg Trained regressor
   *@param name Path to save the plot
   */
  def plotReg(data: List[List[Double]], labels: List[Double], reg: Regressor, name: String="plots/reg.pdf"): Unit = {
    val f = Figure()
    f.visible= false
    val p = f.subplot(0)

    val dataPerFeature = data.transpose
    val xMeans = for (feat <- dataPerFeature) yield feat.sum / feat.length

    for (i <- 0 until dataPerFeature.length){
      val col: String = StringTrafo.convertToColorCode(PaintScale.Category10(i))

      val x = dataPerFeature(i)
      p += plot(x, labels, '.', colorcode=col, name= "Feature " + i)

      // get equidistant points in this feature for line plotting
      val equiVec: DenseVector[Double] = linspace(x.min, x.max, 200)
      val xEqui: List[Double] = (for (i <- 0 until equiVec.size) yield equiVec(i)).toList
      // create new data, equidistant in this feature, respective mean in all other features
      val xEquiMean: List[List[Double]] = for (xe <- xEqui) yield
        (for (j <- 0 until dataPerFeature.length) yield if (i == j) xe else xMeans(j)).toList
      val y = reg.predict(xEquiMean)
      p += plot(xEqui, y, '-', colorcode=col, name= "Prediction " + i)
      // p += plot(xEqui, y, '-', colorcode= "[50,200,100]", name= "reg " + i)
    }

    p.ylabel = "Label"
    if (data.head.length == 1) p.xlabel = "Feature 0"
    p.legend = true
    p.title = reg.name + " prediction"

    f.saveas(name)
  }
}
