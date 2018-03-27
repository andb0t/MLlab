package datastructures

import utils._


object Optimizer {
  /** Determines the function maximum by random walks */
  def optimize(func: (List[Double]) => Double, startParams: List[Double], startRanges: List[List[Double]]): Tuple2[Double, List[Double]] = {

    def maximize(count: Int, maximum: Double, params: List[Double], ranges: List[List[Double]]): List[Double] = {
      val nSteps = 1000
      val numberDimensions: Int = ranges.length
      if (count == nSteps) {
        println(s"- final step $count: optimum %.3f, params ".format(maximum) +
        "(" + params.map(p => Maths.round(p, 3)).mkString(", ") + ")"
        )
        params
      }
      else {
        if (count % 100 == 0 || (count < 50 && count % 10 == 0) || (count < 5))
          println(s"- optimization step $count: optimum %.3e, params ".format(maximum) +
          "(" + params.map(p => Maths.round(p, 3)).mkString(", ") + ")"
        )
        val dimension: Int = scala.util.Random.nextInt(numberDimensions)
        val sign: Int = scala.util.Random.nextInt(2) * 2 - 1
        val step: Double = 1.0 * sign * (ranges(dimension)(1) - ranges(dimension).head) / 100
        // println(s"Step $count: step %.3f in dimension $dimension".format(step))
        val newParams: List[Double] = params.zipWithIndex.map{case (p, i) => if (i == dimension) p + step else p}
        val newMaximum: Double = func(newParams)
        // if (newMaximum > maximum) println("New maximum " + maximum + " at " + params)
        if (newMaximum > maximum) maximize(count+1, newMaximum, newParams, ranges)
        else maximize(count+1, maximum, params, ranges)
      }
    }

    val params: List[Double] = maximize(0, Double.MinValue, startParams, startRanges)
    (params.head, params.tail)
  }
}
