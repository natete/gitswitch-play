package models

import play.api.libs.json.{Format, Json}

/**
  * Created by natete on 14/04/17.
  */
case class User(id: Option[Long], username: String, shaPassword: Array[Byte])

object User {
  implicit val jsonFormat: Format[User] = Json.format[User]
}