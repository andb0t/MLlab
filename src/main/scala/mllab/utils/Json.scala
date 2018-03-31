package json

import play.api.libs.json.{JsValue, Json, JsNull}


/** Provides JSON conversion functions */
object JsonMagic {

  /** Gets a String from a JSON object */
  def toString(json: JsValue, key: String, default: String): String =
    try
      json(key).toString().replaceAll("^\"|\"$", "")
    catch
    {
      case _: java.util.NoSuchElementException => default
      case _: java.lang.Exception => default
    }

  /** Gets an Int from a JSON object */
  def toInt(json: JsValue, key: String, default: Int): Int =
    try
      json(key).toString().toInt
    catch
    {
      case _: java.util.NoSuchElementException => default
      case _: java.lang.Exception => default
    }

  /** Gets an Boolean from a JSON object */
  def toBoolean(json: JsValue, key: String, default: Boolean): Boolean =
    try
      json(key).toString().toBoolean
    catch
    {
      case _: java.util.NoSuchElementException => default
      case _: java.lang.Exception => default
    }

  /** Gets a Double from a JSON object */
  def toDouble(json: JsValue, key: String, default: Double): Double =
    try
      json(key).toString().toDouble
    catch
    {
      case _: java.util.NoSuchElementException => default
      case _: java.lang.Exception => default
    }

  /** Gets a List[Int] from a JSON object */
  def toListInt(json: JsValue, key: String, default: List[Int]): List[Int] =
    try
      json(key).toString().split(',').map(_.replaceAll("[^0-9]", "").toInt).toList
    catch
    {
      case _: java.util.NoSuchElementException => default
      case _: java.lang.Exception => default
    }

  /** Gets a List[List[Double]] from a JSON object */
  def toListListDouble(json: JsValue, key: String, default: List[List[Double]]): List[List[Double]] =
    try
      Nil
    catch
    {
      case _: java.util.NoSuchElementException => default
      case _: java.lang.Exception => default
    }

  /** Parses a string into argument value pairs */
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
