import org.rogach.scallop._
import play.api.libs.json.{JsValue, Json}

import human._


object CatEncounter {
  class Conf(arguments: Seq[String]) extends ScallopConf(arguments) {
    val hyper = opt[String](
      default = Some("animal=cat, color=black, count=2"),
      descr = "hyperparameters to pass to the algorithm"
    )
    verify()
  }

  def jsonify(hyper: String): JsValue = {
    val args = hyper.split(',').map(text => text.split("=").map(_.trim))
    val argsMap: Map[String, String] = args.map(a => (a(0) -> a(1))).toMap
    Json.toJson(argsMap)
  }

  def simpleJson(): JsValue = {
    // val json = Json.obj("color" -> "black", "animal" -> "cat")
    val json = Json.obj("color" -> "black", "animal" -> "cat", "count" -> "2")
    json
  }

  def main(args: Array[String]): Unit = {
    val conf = new Conf(args)
    if (!conf.hyper().isEmpty())
      println("Chosen hyperparameters: " + conf.hyper)

    // val json = simpleJson()
    val json = jsonify(conf.hyper())
    val hum = Human.human2(json)
    println(hum)
    hum.ask()
  }
}
