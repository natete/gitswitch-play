package simplegit.account.controllers

import javax.inject.Inject

import authentication.controllers.security.SecuredController
import authentication.service.AuthenticationService
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent}
import simplegit.account.common.services.GitServiceFactory
import simplegit.account.dao.AccountDAO
import simplegit.account.model.GitAccountRequest
import util.ResponseUtil

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by natete on 16/04/17.
  */
class AccountController @Inject()(accountDAO: AccountDAO,
                                  gitServiceFactory: GitServiceFactory,
                                  implicit private val authenticationService: AuthenticationService,
                                  implicit override val executionContext: ExecutionContext) extends SecuredController {

  def getAccounts: Action[AnyContent] = AuthenticatedActionWithPayload {
    (_, tokenPayload) =>

      accountDAO.getAccounts(tokenPayload.userId).map(accounts => Ok(Json.toJson(accounts)))
  }

  def addAccount(): Action[JsValue] = AuthenticatedActionWithPayload(parse.json) {
    (request, tokenPayload) =>

      request.body.validate[GitAccountRequest].fold(
        errors => Future.successful(BadRequest(ResponseUtil.errorToRestResponse(errors.flatMap(_._2).map(_.message).head).json)),
        gitAccountRequest =>

          gitServiceFactory
            .getGitService(gitAccountRequest.accountType.getOrElse("GITHUB"))
            .authorizeCode(gitAccountRequest.code, gitAccountRequest.state, tokenPayload.userId)
            .map(account => Created(Json.toJson(account)))
            .recoverWith { case _ => Future.successful(Conflict(ResponseUtil.errorToRestResponse("already exists").json)) }
      )
  }

  def removeAccount(accountId: Long): Action[AnyContent] = AuthenticatedActionWithPayload {
    (_, tokenPayload) =>
      gitServiceFactory
        .getGitService()
        .removeAccount(accountId, tokenPayload.userId)
        .map(_ => Ok)
        .recoverWith { case _ => Future.successful(NotFound(ResponseUtil.errorToRestResponse("Unable to delete selected account").json)) }

  }
}
