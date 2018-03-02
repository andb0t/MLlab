package plot

import breeze.linalg._
import breeze.plot._


object Plot {

  def plotThis(): Unit = {
    println("Plot this!")
    val f = Figure()
    val p = f.subplot(0)
    val x = linspace(0.0,1.0)
    p += plot(x, x :^ 2.0)
    p += plot(x, x :^ 3.0, '.')
    p.xlabel = "x axis"
    p.ylabel = "y axis"
    println("Now save the plot")
    f.saveas("lines.png") // save current figure as a .png, eps and pdf also supported
  }
}
