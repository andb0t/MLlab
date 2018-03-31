package human

import play.api.libs.json.{JsValue, Json}

object Human {
  case class FuncArguments(animal: String, color: String, count: String)
  implicit val funcArgumentsFormat = Json.format[FuncArguments]
  implicit def jsValueToFuncArguments(json: JsValue): FuncArguments = json.as[FuncArguments]
  def human2(json: JsValue): Human = {
    (Human.apply _).tupled(FuncArguments.unapply(json).get)
  }
}

case class Human(animal: String, color: String, count: String) {
  println(s"Initialize human who saw the same $color $animal $count times'")

  def ask(): Unit =
    println("'I saw %s different %s %ss!'".format(count, color, animal))

  override def toString(): String =
    "A human who thinks he saw %s different %s %ss".format(count, color, animal)

}
