package authentication.model

import play.api.libs.json._

/**
  * Created by natete on 15/04/17.
  */
case class ResponseToken(access_token: String, expires_in: Int, token_type: String = "Bearer")

object ResponseToken {
  implicit val jsonFormat: Format[ResponseToken] = Json.format[ResponseToken]
}

