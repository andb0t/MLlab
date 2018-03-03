package plotting

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
}
