package json

import play.api.libs.json.{JsValue, Json, JsNull}

import utils._


/** Provides JSON conversion functions */
object JsonMagic {

/** Retrieves a string from a JSON object and formats it to be usable */
  def getValue(json: JsValue, key: String): String =
    json(key).toString().replaceAll("^\"|\"$", "")

  /** Gets a String from a JSON object */
  def toString(json: JsValue, key: String, default: String): String =
    try
      getValue(json, key)
    catch
    {
      case _: java.util.NoSuchElementException => default
    }

  /** Gets an Int from a JSON object */
  def toInt(json: JsValue, key: String, default: Int): Int =
    try
      getValue(json, key).toInt
    catch
    {
      case _: java.util.NoSuchElementException => default
    }

  /** Gets an Boolean from a JSON object */
  def toBoolean(json: JsValue, key: String, default: Boolean): Boolean =
    try
      getValue(json, key).toBoolean
    catch
    {
      case _: java.util.NoSuchElementException => default
    }

  /** Gets a Double from a JSON object */
  def toDouble(json: JsValue, key: String, default: Double): Double =
    try
      getValue(json, key).toDouble
    catch
    {
      case _: java.util.NoSuchElementException => default
    }

  /** Gets a List[Int] from a JSON object */
  def toListInt(json: JsValue, key: String, default: List[Int]): List[Int] =
    try
      getValue(json, key).split(',').map(_.replaceAll("[^0-9]", "").toInt).toList
    catch
    {
      case _: java.util.NoSuchElementException => default
    }

  /** Gets a List[Double] from a JSON object */
  def toListDouble(json: JsValue, key: String, default: List[Double]): List[Double] =
    try
      getValue(json, key).split(',').map(_.replaceAll("""[^0-9\.]""", "").toDouble).toList
    catch
    {
      case _: java.util.NoSuchElementException => default
    }

  /** Gets a List[List[Double]] from a JSON object */
  def toListListDouble(json: JsValue, key: String, default: List[List[Double]]): List[List[Double]] =
    try
    {
      val str = StringTrafo.between(getValue(json, key))
      val firstSplit = StringTrafo.splitString(str).map(StringTrafo.between(_))
      val secondSplit = firstSplit.map(StringTrafo.splitString(_))
      secondSplit.map(_.map(_.replaceAll("""[^0-9\.]""", "").toDouble))
    }
    catch
    {
      case _: java.util.NoSuchElementException => default
    }

  /** Parses a string into argument value pairs */
  def jsonify(str: String, verbose: Boolean= true): JsValue = {
    val args = StringTrafo.splitString(str).map(text => text.split("=").map(_.trim))
    println(args.mkString("\n"))
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
