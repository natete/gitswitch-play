package authentication.controllers.security

import authentication.model.TokenPayload
import authentication.service.AuthenticationService
import authentication.util.JwtUtil
import models.User
import play.api.mvc._
import util.ResponseUtil

import scala.concurrent.{ExecutionContext, Future}

abstract class SecuredController(implicit private val authenticationService: AuthenticationService,
                                 implicit val executionContext: ExecutionContext
                                ) extends Controller {

  implicit private final val SECRET = authenticationService.getJwtSecret

  private final val INVALID_TOKEN_ERROR = "Invalid authentication token"
  private final val MISSING_TOKEN_ERROR = "Missing authentication token"
  private final val AUTH_TOKEN_NOT_FOUND_ERROR = "Authorization token not found in secured endpoint"
  private final val AUTHORIZATION_TOKEN_VALID_START = "Bearer "

  def AuthenticatedActionWithPayload(block: (Request[AnyContent], TokenPayload) => Future[Result]): Action[AnyContent] = {
    AuthenticatedActionWithPayload(parse.anyContent)(block)
  }

  def AuthenticatedActionWithPayload[A](bodyParser: BodyParser[A])(block: (Request[A], TokenPayload) => Future[Result]): Action[A] = {
    Action.async(bodyParser) {
      request =>
        request.headers.get(AUTHORIZATION).map(token => {
          authenticationService.validateToken(token).flatMap(
            isValidToken =>
              if (!isValidToken)
                Future.successful(
                  Unauthorized(ResponseUtil.errorToRestResponse(INVALID_TOKEN_ERROR).json)
                )
              else for {
                tokenPayload <- JwtUtil.getPayloadIfValidToken[TokenPayload](token.replaceFirst(AUTHORIZATION_TOKEN_VALID_START, ""))
                response: Result <- block(request, tokenPayload.get)
              } yield response
          )
        }).getOrElse(
          Future.successful(
            Unauthorized(ResponseUtil.errorToRestResponse(MISSING_TOKEN_ERROR).json)
          ))
    }
  }


  implicit def userFromSecuredRequest(implicit request: Request[_]): Future[User] = {
    val token = request.headers.get(AUTHORIZATION)
      .getOrElse(throw new IllegalStateException(AUTH_TOKEN_NOT_FOUND_ERROR))

    this.authenticationService.getUserFromToken(token)
  }
}
