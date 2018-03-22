package datastructures


object DataTrafo {
  def addPolyFeatures(X: List[List[Double]], degree: Int): List[List[Double]] = {
    if (degree == 1) X
    else {
      val xFeatures = X.transpose
      val nFeatures = xFeatures.length

      def polyMap(deg: Int): List[Map[Int, Int]] = {

        // def orderMaps(deg: Int, nFeat: Int, list: List[Int], thisDeg: Int = 2): List[Int] = {
        //   def addOne(list: List[Int]): List[List[Int]] =
        //       for (i <- 0 until list.length) yield
        //       list.map(l =>)
        //
        //   if (deg < 2) list
        //   else {
        //     val thisDegreeList =
        //     orderMaps(deg-1, nFeat, thisDegreeList)
        //   }
        // }
        //
        // val resultMaps = orderMaps(degree, nFeatures, List.fill(nFeatures)(0))

        val maps = for (i <- 2 to degree) yield for (j <- 0 until nFeatures) yield Map(j -> i)
        val finalMap: List[Map[Int, Int]] = maps.flatten.toSet.toList

        println("Final polymap (feature -> order):")
        println(finalMap.mkString("\n"))
        finalMap
      }

      def addFeatures(xExtended: List[List[Double]], featureMapList: List[Map[Int, Int]]): List[List[Double]] = featureMapList match {
        case Nil => xExtended
        case mHead::mTail => {
          val indices: List[Int] = (for (m <- mHead) yield List.fill(m._2)(m._1)).flatten.toList
          val newFeature: List[Double] = for (x <- X) yield indices.map(i => x(i)).product
          addFeatures(newFeature::xExtended, mTail)
        }
      }

      val xExtended = addFeatures(xFeatures, polyMap(degree)).transpose
      // println("Instances with the transformed features:")
      // println(X.head + " -> " + xExtended.head)
      // println(X(1) + " -> " + xExtended(1))
      // println(X(2) + " -> " + xExtended(2))
      // println(X(3) + " -> " + xExtended(3))
      xExtended
    }
  }
}
