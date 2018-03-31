package json

import play.api.libs.json.{JsValue, Json}


object JsonMagic {
  def stringify(json: JsValue, key: String, default: String): String =
    try
      json(key).toString().replaceAll("^\"|\"$", "")
    catch
    {
      case foo: java.util.NoSuchElementException => default
    }

    def jsonify(hyper: String): JsValue = {
      val args = hyper.split(',').map(text => text.split("=").map(_.trim))
      val argsMap: Map[String, String] = args.map(a => (a(0) -> a(1))).toMap
      Json.toJson(argsMap)
    }

}
