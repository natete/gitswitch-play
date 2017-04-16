package authentication.model

import org.joda.time.DateTime
import play.api.libs.json.{Format, Json}

/**
  * Created by natete on 15/04/17.
  */
case class TokenPayload(userId: Long, username: String, expiration: DateTime)

object TokenPayload {
  implicit val jsonFormat: Format[TokenPayload] = Json.format[TokenPayload]
}