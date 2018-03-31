package human

import play.api.libs.json.JsValue

import json._

/**
 * @todo pick up default arguments from main constructor
 */
case class Human(color: String, animal: String, count: String="0") {
  def this(json: JsValue) = {
    this(animal=JsonMagic.stringify(json, "animal", "object"),
         color=JsonMagic.stringify(json, "color", "invisible"),
         count=JsonMagic.stringify(json, "count", "0"))
  }

  println(s"Initialize human who saw the same $color $animal $count times'")

  def ask(): Unit =
    println("'I saw %s different %s %ss!'".format(count, color, animal))

  override def toString(): String =
    "A human who thinks he saw %s different %s %ss".format(count, color, animal)

}
