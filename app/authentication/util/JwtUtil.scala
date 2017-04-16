package authentication.util

import authentication.model.JwtSecret
import com.nimbusds.jose.{JWSAlgorithm, JWSHeader, JWSObject, Payload}
import play.api.libs.json.{JsValue, Json, Reads, Writes}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by natete on 15/04/17.
  */
object JwtUtil {

  def signJwtPayload(payload: String)(implicit secret: JwtSecret): String = {
    val jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.HS256), new Payload(payload))
    jwsObject.sign(secret.signer)
    jwsObject.serialize()
  }

  def signJwtPayload(payload: JsValue)(implicit secret: JwtSecret): String = {
    this.signJwtPayload(payload.toString)
  }

  def signJwtPayload[T](payload: T)(implicit secret: JwtSecret, jsonWrites: Writes[T]): String = {
    this.signJwtPayload(Json.toJson(payload))
  }

  def getPayloadIfValidToken[T](token: String)(implicit secret: JwtSecret, jsonWrites: Reads[T]): Future[Option[T]] = {
    this.tryGetPayloadStringIfValidToken(token).flatMap {
      case Some(sv) => Future.successful(Some(Json.parse(sv).as[T]))
      case None => Future.successful(None)
    }
  }

  def tryGetPayloadStringIfValidToken(token: String)(implicit secret: JwtSecret): Future[Option[String]] = {
    try {
      val jWSObject = JWSObject.parse(token)

      if (jWSObject.verify(secret.verifier)) {
        Future.successful(Some(jWSObject.getPayload.toString))
      } else {
        Future.successful(None)
      }
    } catch {
      case _: Throwable => Future.successful(None)
    }
  }
}
