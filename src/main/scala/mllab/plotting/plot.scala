package plotting

import classifiers._

import breeze.linalg._
import breeze.plot._


object Plotting {

  def plotData(data: List[List[Double]], labels: List[Int], name: String="plot.pdf"): Unit = {
    val f = Figure()
    f.visible= false
    val p = f.subplot(0)
    for (category: Int <- labels.toSet) {
      val filteredData: List[List[Double]] = (data zip labels).filter(_._2 == category).map(_._1)
      val x: List[Double] = filteredData.map(e => e.head)
      val y: List[Double] = filteredData.map(e => e(1))
      p += plot(x, y, '.')
    }
    p.xlabel = "Feature 0"
    p.ylabel = "Feature 1"
    f.saveas(name)
  }

  def plotClf(data: List[List[Double]], labels: List[Int], clf: Classifier, name: String="clf.pdf"): Unit = {
    val predictions = clf.predict(data)
    val f = Figure()
    f.visible= false
    val p = f.subplot(0)
    // now plot all datapoints
    for (category: Int <- predictions.toSet) {
      val filteredData: List[List[Double]] = (data zip predictions).filter(_._2 == category).map(_._1)
      val x: List[Double] = filteredData.map(e => e.head)
      val y: List[Double] = filteredData.map(e => e(1))
      p += plot(x, y, '.')
    }

    val wrong: List[Boolean] = (predictions zip labels).map{case (x, y) => x != y}
    val filteredData: List[List[Double]] = (data zip wrong).filter(_._2).map(_._1)
    val x: List[Double] = filteredData.map(e => e.head)
    val y: List[Double] = filteredData.map(e => e(1))
    p += plot(x, y, '+', colorcode= "r")

    p.xlabel = "Feature 0"
    p.ylabel = "Feature 1"
    f.saveas(name)

  }

  def plotGrid(data: List[List[Double]], clf: Classifier, name: String="grid.pdf"): Unit = {

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
    for (category: Int <- predictions.toSet) {
      val filteredData: List[List[Double]] = (gridData zip predictions).filter(_._2 == category).map(_._1)
      val x: List[Double] = filteredData.map(e => e.head)
      val y: List[Double] = filteredData.map(e => e(1))
      p += plot(x, y, '.')
    }
    p.xlabel = "Feature 0"
    p.ylabel = "Feature 1"
    f.saveas(name)
  }
}
