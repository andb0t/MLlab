package human

import play.api.libs.json.{JsValue, Json}

object human {
  case class FuncArguments(animal: String, color: String, order: Int)
  implicit val funcArgumentsFormat = Json.format[FuncArguments]
  implicit def jsValueToFuncArguments(json: JsValue): FuncArguments = json.as[FuncArguments]
  def human2(json: JsValue): Unit = {
    (human.apply _).tupled(FuncArguments.unapply(json).get)
  }
}

case class human(animal: String, color: String, order: Int) {
  println(s"$order. human says: 'Oh, a $color $animal!'")
}
