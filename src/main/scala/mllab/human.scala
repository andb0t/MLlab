package human

import play.api.libs.json.JsValue

import json._


object Human {
  val animal: String = "object"
  val color: String = "invisible"
  val count: Int = 1337
  val size: Double = 13.37
  val list: List[Int] = List(1, 3, 3, 7)
  val listList: List[List[Double]] = Nil
}

case class Human(listList: List[List[Double]] = Human.listList, color: String=Human.color, animal: String=Human.animal, count: Int=Human.count, size: Double=Human.size, list: List[Int]=Human.list) {
  def this(json: JsValue) = {
    this(animal=JsonMagic.toString(json, "animal", Human.animal),
         color=JsonMagic.toString(json, "color", Human.color),
         count=JsonMagic.toInt(json, "count", Human.count),
         list=JsonMagic.toListInt(json, "list", Human.list),
         listList=JsonMagic.toListListDouble(json, "listList", Human.listList),
         size=JsonMagic.toDouble(json, "size", Human.size))
  }

  println(s"Initialize human who saw the same $color $animal $count times'")
  println(list)
  println(listList)

  def ask(): Unit =
    println("'I saw %d different %s %ss!'".format(count, color, animal))

  override def toString(): String =
    "A human who thinks he saw %d different %s %ss of size %.2f m each".format(count, color, animal, size)

}
