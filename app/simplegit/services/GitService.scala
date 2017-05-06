package simplegit.services

import play.api.Configuration
import play.api.libs.ws.WSClient
import simplegit.account.dao.AccountDAO
import simplegit.account.model.Account
import simplegit.repository.model.Repository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by natete on 16/04/17.
  */
abstract class GitService {

  protected val ws: WSClient
  protected val accountDAO: AccountDAO
  protected val configuration: Configuration

  def authorizeCode(code: String, state: Option[String], ownerId: Long): Future[Account]

  def getAccount(token: String): Future[Account]

  def getRepositories(token: String): Future[Seq[Repository]]

  def getRepositories(accounts: Seq[Account]): Seq[Repository] = {
    val repositories: Seq[Repository] = Nil

    accounts.foreach(
      account => {
        for {
          repos <- this.getRepositories(account.token)
        } {repositories ++ repos}
      })

    repositories
    //    for {
    //      reposRequests <- accounts.map(account => this.getRepositories(account.token))
    //      repos <- reposRequests
    //    } yield reposRequests

  }

  def getAllAccounts(userId: Long): Future[Seq[Account]] =
    accountDAO.getAccounts(userId)

  def saveAccount(account: Account): Future[Account] =
    accountDAO.create(account)

  def removeAccount(accountId: Int, userId: Long): Future[Unit] =
    accountDAO.delete(accountId, userId)
}
