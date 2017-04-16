package simplegit.account.model

import play.api.libs.json.{Format, Json}

/**
  * Created by natete on 16/04/17.
  */
case class GitAccountRequest(code: String, state: Option[String], accountType: Option[String])

object GitAccountRequest {
  implicit val jsonFormat: Format[GitAccountRequest] = Json.format[GitAccountRequest]
}