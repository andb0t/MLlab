package mllab

class Reader(var filePath: String) {

  val content = new Data()

  println("Instantiating a reader!")

  def loadFile(): Unit = {
    println("Load the file " + filePath)
    for( i <- 0 to 3){
      // fill with dummy values for now
      var contentList = List[Float](i + 0, i + 1)
      content.addInstance(contentList)
    }
  }
}
