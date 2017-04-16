package simplegit.account.common.services

import play.api.Configuration
import play.api.libs.ws.WSClient
import simplegit.account.dao.AccountDAO
import simplegit.account.model.Account

import scala.concurrent.Future

/**
  * Created by natete on 16/04/17.
  */
abstract class GitService {

  val ws: WSClient
  val accountDAO: AccountDAO
  val configuration: Configuration

  def authorizeCode(code: String, state: Option[String], ownerId: Long): Future[Account]

  def getAccount(token: String): Future[Account]

  def saveAccount(account: Account): Future[Account] = {
    accountDAO.create(account)
  }

  def removeAccount(accountId: Long, userId: Long): Future[Unit] = {
    accountDAO.delete(accountId, userId)
  }

}
