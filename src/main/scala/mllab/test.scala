import org.rogach.scallop._
import play.api.libs.json.{JsValue, Json}

import human._


object CatEncounter {
  class Conf(arguments: Seq[String]) extends ScallopConf(arguments) {
    val hyper = opt[String](
      default = Some("animal=cat, color=black"),
      descr = "hyperparameters to pass to the algorithm"
    )
    verify()
  }

  def main(args: Array[String]): Unit = {
    val conf = new Conf(args)
    if (!conf.hyper().isEmpty())
      println("Chosen hyperparameters: " + conf.hyper)

    val json = Json.obj("color" -> "black", "animal" -> "cat", "count" -> 2)
    // val json = Json.obj("color" -> "black", "animal" -> "cat")
    val hum = Human.human2(json)
    println(hum)
    hum.ask()
  }
}
