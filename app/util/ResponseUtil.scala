package util

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, JsValue, Json, Writes}

/**
  * Created by natete on 15/04/17.
  */
case class RestResponse[T: Writes](data: Option[T], messages: Option[List[String]] = Some(List())) {
  selfRef =>
  lazy val json: JsValue = Json.toJson(selfRef)
}

object RestResponse {
  implicit def writes[T: Writes]: Writes[RestResponse[T]] = (
    (JsPath \ "data").writeNullable[T] and
      (JsPath \ "messages").writeNullable[List[String]]
    ) (unlift(RestResponse.unapply[T]))
}

object ResponseUtil {

  def errorsToRestResponse(errors: List[String]): RestResponse[JsValue] = RestResponse(Option.empty[JsValue], Some(errors))

  def errorToRestResponse(error: String): RestResponse[JsValue] = RestResponse(Option.empty[JsValue], Some(List(error)))

  def dataToRestResponse[T: Writes](data: T): RestResponse[T] = RestResponse(Some(data), None)
}
