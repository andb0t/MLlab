import org.rogach.scallop._
import play.api.libs.json.{JsValue, Json}

import human._
import json._
import utils._


object CatEncounter {
  class Conf(arguments: Seq[String]) extends ScallopConf(arguments) {
    val hyper = opt[String](
      default = Some(""),
      descr = "hyperparameters to pass to the algorithm"
    )
    verify()
  }

  def main(args: Array[String]): Unit = {
    val conf = new Conf(args)
    println(conf.hyper())
    val json = JsonMagic.jsonify(conf.hyper(), verbose= true)
    val humjson = new Human(json)
    println(humjson)
    humjson.ask()

    println("my splitter:")
    println(StringTrafo.splitString(conf.hyper()).mkString("\n"))
  }
}
