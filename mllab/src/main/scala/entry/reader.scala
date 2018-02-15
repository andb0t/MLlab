package mllab

class Reader(var filePath: String) {

  val content = Array.ofDim[Int](4, 2)

  println("Instantiating a reader!")

  def loadFile(): Unit = {
    println("Load the file " + filePath)
    for( i <- 0 to 3){
      for( j <- 0 to 1){
        // fill with dummy values for now
        content(i)(j) = i + j
      }
    }
  }
}
