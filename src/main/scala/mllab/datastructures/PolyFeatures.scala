package datastructures


object DataTrafo {

  /** Produces a list of all possible multiplicities of at least 2 features */
  def polyList(deg: Int, nFeat: Int, list: List[List[Int]]): List[List[Int]] = {
    def oneHot(idx: Int): List[Int] =
      (for (i <- 0 until nFeat) yield if (i == idx) 1 else 0).toList
    def addOne(list: List[Int]): List[List[Int]] = list match {
        case Nil => (for (i <- 0 until nFeat) yield oneHot(i)).toList
        case _ =>
          (for (i <- 0 until nFeat) yield list.zip(oneHot(i)).map{case (x, y) => x + y}).toList
      }
    if (deg == 0) list
    else {
      val thisDegreeList =
        if (list == Nil) (for (i <- 0 until nFeat) yield oneHot(i)).toList
        else if (deg == 1) (list.map(l => addOne(l))).flatten  // don't record unit multiplicities
        else (list.map(l => addOne(l))).flatten ::: list
      polyList(deg-1, nFeat, thisDegreeList.toSet.toList)
    }
  }

    /** Turns turns the lists with multiplicities into polynomial mapping */
  def polyMap(order: Int, nFeatures: Int): List[Map[Int, Int]] =
    polyList(order, nFeatures, Nil).map(l => (for (i <- 0 until l.length) yield i -> l(i)).toMap)

  /** Adds polynomial features of up until given order */
  def addPolyFeatures(X: List[List[Double]], order: Int): List[List[Double]] = {
    if (order == 1) X
    else {
      val xFeatures = X.transpose
      val nFeatures = xFeatures.length

      def addFeatures(xExtended: List[List[Double]], featureMapList: List[Map[Int, Int]]): List[List[Double]] = featureMapList match {
        case Nil => xExtended
        case mHead::mTail => {
          val indices: List[Int] = (for (m <- mHead) yield List.fill(m._2)(m._1)).flatten.toList
          val newFeature: List[Double] = for (x <- X) yield indices.map(i => x(i)).product
          addFeatures(newFeature::xExtended, mTail)
        }
      }
      val polyMaps = polyMap(order, nFeatures)
      // println("Polynomial mapping (%d maps):".format(polyMaps.length))
      // println(polyMaps.sortBy(_.values).mkString("\n"))
      val xExtended = addFeatures(xFeatures, polyMaps).transpose
      println("Instances with added polynomial features (%d -> %d features)".format(X.head.length, xExtended.head.length))
      // println(X.head + " -> " + xExtended.head)
      // println(X(1) + " -> " + xExtended(1))
      // println(X(2) + " -> " + xExtended(2))
      // println(X(3) + " -> " + xExtended(3))
      xExtended
    }
  }
}
