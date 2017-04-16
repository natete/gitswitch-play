package models

import play.api.libs.json.{Format, Json}

/**
  * Created by natete on 15/04/17.
  */
case class Credentials(username: String, password: String)

object Credentials {
  implicit val jsonFormat: Format[Credentials] = Json.format[Credentials]
}
