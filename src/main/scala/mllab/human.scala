package human

import play.api.libs.json.JsValue

import json._

/**
 * @todo pick up default arguments from main constructor
 */
case class Human(color: String, animal: String, count: Int, size: Double, list: List[Int]) {
  def this(json: JsValue) = {
    this(animal=JsonMagic.toString(json, "animal", "object"),
         color=JsonMagic.toString(json, "color", "invisible"),
         count=JsonMagic.toInt(json, "count", 0),
         list=JsonMagic.toListInt(json, "list", List(0, 1, 2)),
         size=JsonMagic.toDouble(json, "size", 0.0))
  }

  println(s"Initialize human who saw the same $color $animal $count times'")
  for (i <- 0 until list.length)
    println(list(i))

  def ask(): Unit =
    println("'I saw %d different %s %ss!'".format(count, color, animal))

  override def toString(): String =
    "A human who thinks he saw %d different %s %ss of size %.2f m each".format(count, color, animal, size)

}
