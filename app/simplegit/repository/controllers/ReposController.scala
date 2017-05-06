package simplegit.repository.controllers

import javax.inject.Inject

import authentication.controllers.security.SecuredController
import authentication.service.AuthenticationService
import play.api.libs.json.Json
import simplegit.services.GitServiceFactory

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

/**
  * Created by natete on 17/04/17.
  */
class ReposController @Inject()(gitServiceFactory: GitServiceFactory,
                                implicit private val authenticationService: AuthenticationService,
                                implicit override val executionContext: ExecutionContext) extends SecuredController {

  def getRepositories = AuthenticatedActionWithPayload {
    (_, tokenPayload) =>

      for {
        accounts <- gitServiceFactory.getGitService().getAllAccounts(tokenPayload.userId)
        reposRequests <- Future.successful(accounts.map(account => gitServiceFactory.getGitService(account.accountType).getRepositories(account.token)))
        repos <- Future.sequence(reposRequests.map(futureToFutureTry(_))).map(_.collect { case Success(x) => x })
      } yield Ok(Json.toJson(repos.flatten))
  }

  private def futureToFutureTry[T](f: Future[T]): Future[Try[T]] =
    f.map(Success(_)).recover({ case e => Failure(e) })
}
