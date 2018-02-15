package mllab

class Analyzer(data: List[List[Float]]) {

  for (instance <- data){
    val result: Float = instance.reduce(_ + _) / instance.length
    println("Result is " + result)
  }
}
