package authentication.controllers

import javax.inject.Inject

import authentication.service.AuthenticationService
import dao.UserDAO
import models.Credentials
import play.api.libs.Files
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, Controller, MultipartFormData}
import util.ResponseUtil

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by natete on 14/04/17.
  */
class AuthController @Inject()(userDAO: UserDAO,
                               implicit private val authenticationService: AuthenticationService,
                               implicit val executionContext: ExecutionContext) extends Controller {

  private final val BAD_EMAIL_OR_PASSWORD_ERROR = "Bad email or password"

  def authenticate: Action[MultipartFormData[Files.TemporaryFile]] = Action.async(parse.multipartFormData) { implicit request =>

    val formData = request.body.asFormUrlEncoded
    val username = formData("username").head
    val password = formData("password").head
    val credentials = new Credentials(username, password)

    authenticationService.authenticate(credentials.username, credentials.password).map {
      case Some(token) => Ok(Json.toJson(token))
      case None => Unauthorized(ResponseUtil.errorToRestResponse(BAD_EMAIL_OR_PASSWORD_ERROR).json)
    }
  }


  def signup: Action[JsValue] = Action.async(parse.json) { implicit request =>

    request.body.validate[Credentials].fold(
      errors => Future.successful(BadRequest(ResponseUtil.errorToRestResponse(errors.flatMap(_._2).map(_.message).head).json)),
      credentials => userDAO.create(credentials.username, credentials.password).map(_ => Created)
    )
  }
}
