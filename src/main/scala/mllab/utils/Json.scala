package json

import play.api.libs.json.{JsValue, Json, JsNull}


object JsonMagic {
  def toString(json: JsValue, key: String, default: String): String =
    try
      json(key).toString().replaceAll("^\"|\"$", "")
    catch
    {
      case _: java.util.NoSuchElementException => default
      case _: java.lang.Exception => default
    }

  def toInt(json: JsValue, key: String, default: Int): Int =
    try
      json(key).toString().toInt
    catch
    {
      case _: java.util.NoSuchElementException => default
      case _: java.lang.Exception => default
    }

  def toDouble(json: JsValue, key: String, default: Double): Double =
    try
      json(key).toString().toDouble
    catch
    {
      case _: java.util.NoSuchElementException => default
      case _: java.lang.Exception => default
    }

  def toListInt(json: JsValue, key: String, default: List[Int]): List[Int] =
    try
      json(key).toString().split(',').map(_.replaceAll("[^0-9]", "").toInt).toList
    catch
    {
      case _: java.util.NoSuchElementException => default
      case _: java.lang.Exception => default
    }

  def jsonify(str: String, verbose: Boolean= true): JsValue = {
    val regex = """,(?![^\(\[]*[\]\)])"""
    val args = str.split(regex).map(text => text.split("=").map(_.trim))
    try {
      val argsMap: Map[String, String] = args.map(a => (a(0) -> a(1))).toMap
      val json = Json.toJson(argsMap)
      if (!str.isEmpty() && verbose)
        println(Json.prettyPrint(json))
      json
    }
    catch
    {
      case _: java.lang.ArrayIndexOutOfBoundsException => JsNull
    }
  }
}
