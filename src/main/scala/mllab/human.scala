package human

import play.api.libs.json.JsValue

import json._


case class Human(animal: String, color: String, count: String="1000") {
  def this(json: JsValue) = {
    this(JsonMagic.stringify(json, "animal", "object"),
         JsonMagic.stringify(json, "color", "unvisible"),
         JsonMagic.stringify(json, "count", "1000"))
  }

  println(s"Initialize human who saw the same $color $animal $count times'")

  def ask(): Unit =
    println("'I saw %s different %s %ss!'".format(count, color, animal))

  override def toString(): String =
    "A human who thinks he saw %s different %s %ss".format(count, color, animal)

}
