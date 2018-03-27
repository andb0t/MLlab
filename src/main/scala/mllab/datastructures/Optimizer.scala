package datastructures

import utils._


/** Provides functions for optimizing functions */
object Optimizer {
  /** Determines the function maximum by random walks */
  def optimize(func: (List[Double]) => Double, startParams: List[Double], startRanges: List[List[Double]], silent: Boolean=false): List[Double] = {

    def maximize(count: Int, zooms: Int, maximum: Double, params: List[Double], ranges: List[List[Double]]): List[Double] = {
      val nSteps = params.length * 200
      val numberDimensions: Int = ranges.length
      if (count == nSteps) {
        if (!silent)
          println("- final optim. step% 4d: optimum %.3e, params: ".format(count, maximum) +
            params.map(p => "%+.3f".format(p)).mkString(", ")
          )
        if (zooms == 1) params
        else {
          val zoomFactor = 0.25
          val newWindow: List[Double] = ranges.map(range => zoomFactor * (range(1) - range.head))
          val newRanges: List[List[Double]] = (params zip newWindow).map{case (p, s) => List(p - s / 2, p + s / 2)}
          maximize(0, zooms-1, maximum, params, newRanges)
        }
      }
      else {
        if (!silent && count == 0) {
          println("Optimize within these parameter ranges")
          println((ranges.zipWithIndex.map(ri => "- parameter %d: %.3f - %.3f".format(ri._2, ri._1.head, ri._1(1)))).mkString("\n"))
          println("Optimization:")
       }
        if (!silent && (count % 100 == 0 || (count < 50 && count % 10 == 0) || (count < 5)))
          println("- optimization step% 4d: optimum %.3e, params: ".format(count, maximum) +
            params.map(p => "%+.3f".format(p)).mkString(", ")
        )
        val dimension: Int = scala.util.Random.nextInt(numberDimensions)
        val sign: Int = scala.util.Random.nextInt(2) * 2 - 1
        val step: Double = 1.0 * sign * (ranges(dimension)(1) - ranges(dimension).head) / 10
        // println(s"Step $count: step %.3f in dimension $dimension".format(step))
        val newParams: List[Double] = params.zipWithIndex.map{case (p, i) => if (i == dimension) p + step else p}
        val newMaximum: Double = func(newParams)
        // if (newMaximum > maximum) println("New maximum " + maximum + " at " + params)
        if (newMaximum > maximum) maximize(count+1, zooms, newMaximum, newParams, ranges)
        else maximize(count+1, zooms, maximum, params, ranges)
      }
    }

    maximize(0, 3, Double.MinValue, startParams, startRanges)
  }
}
