package human

import play.api.libs.json.JsValue

import json._


object Human {
  val defaultAnimal: String = "object"
  val defaultColor: String = "invisible"
  val defaultCount: Int = 0
  val defaultSize: Double = 0.01
  val defaultList: List[Int] = List(3, 2, 1, 0)
}

case class Human(color: String=Human.defaultColor, animal: String=Human.defaultAnimal, count: Int=Human.defaultCount, size: Double=Human.defaultSize, list: List[Int]=Human.defaultList) {
  def this(json: JsValue) = {
    this(animal=JsonMagic.toString(json, "animal", Human.defaultAnimal),
         color=JsonMagic.toString(json, "color", Human.defaultColor),
         count=JsonMagic.toInt(json, "count", Human.defaultCount),
         list=JsonMagic.toListInt(json, "list", Human.defaultList),
         size=JsonMagic.toDouble(json, "size", Human.defaultSize))
  }

  println(s"Initialize human who saw the same $color $animal $count times'")
  println(list)

  def ask(): Unit =
    println("'I saw %d different %s %ss!'".format(count, color, animal))

  override def toString(): String =
    "A human who thinks he saw %d different %s %ss of size %.2f m each".format(count, color, animal, size)

}
