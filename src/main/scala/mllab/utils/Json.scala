package json

import play.api.libs.json.{JsValue, Json, JsNull}


object JsonMagic {
  def stringify(json: JsValue, key: String, default: String): String =
    try
      json(key).toString().replaceAll("^\"|\"$", "")
    catch
    {
      case _: java.util.NoSuchElementException => default
      case _: java.lang.Exception => default
    }

  def jsonify(str: String, verbose: Boolean= true): JsValue = {
    val args = str.split(',').map(text => text.split("=").map(_.trim))
    if (args.length <= 1)
      JsNull
    else{
      val argsMap: Map[String, String] = args.map(a => (a(0) -> a(1))).toMap
      val json = Json.toJson(argsMap)
      if (!str.isEmpty() && verbose)
        println(Json.prettyPrint(json))
      json
    }
  }
}
