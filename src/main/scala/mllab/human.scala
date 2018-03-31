package human

import play.api.libs.json.{JsValue, Json}

object Human {
  case class FuncArguments(animal: String, color: String, count: Int)
  implicit val funcArgumentsFormat = Json.format[FuncArguments]
  implicit def jsValueToFuncArguments(json: JsValue): FuncArguments = json.as[FuncArguments]
  def human2(json: JsValue): Human = {
    (Human.apply _).tupled(FuncArguments.unapply(json).get)
  }
}

case class Human(animal: String, color: String, count: Int=2) {
  println(s"Human says: 'Oh, that's $color $animal number $count!'")

  def ask(): Unit =
    println("I saw %d %ss".format(count, animal))

  override def toString(): String =
    "A human who saw %d %ss".format(count, animal)

}
