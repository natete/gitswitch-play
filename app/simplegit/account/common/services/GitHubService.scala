package simplegit.account.common.services

import javax.inject.Inject

import play.api.Configuration
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import simplegit.account.dao.AccountDAO
import simplegit.account.model.Account

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by natete on 16/04/17.
  */
class GitHubService @Inject()(override val ws: WSClient,
                              override val accountDAO: AccountDAO,
                              override val configuration: Configuration) extends GitService {

  private val REGISTER_ACCOUNT_URL = "https://github.com/login/oauth/access_token"
  private val GET_ACCOUNT = "https://api.github.com/user"
  private lazy val CLIENT_ID = configuration.underlying.getString("simplegit.appId")
  private lazy val CLIENT_SECRET = configuration.underlying.getString("simplegit.appSecret")

  override def authorizeCode(code: String, state: Option[String], ownerId: Long): Future[Account] = {

    val payload = Json.obj(
      "client_id" -> CLIENT_ID,
      "client_secret" -> CLIENT_SECRET,
      "code" -> code,
      "state" -> state.get
    )

    for {
      accessTokenResponse <- ws.url(REGISTER_ACCOUNT_URL).withHeaders("Accept" -> "application/json").post(payload)
      account <- getAccount((accessTokenResponse.json \ "access_token").as[String])
      persistedAccount <- saveAccount(account.copy(owner = Some(ownerId)))
    } yield persistedAccount
  }

  override def getAccount(token: String): Future[Account] = {
    ws.url(GET_ACCOUNT).withHeaders("Authorization" -> s"token $token").get().map {
      response =>

        val jsonResponse = response.json
        Account(
          id = None,
          username = (jsonResponse \ "login").as[String],
          fullname = (jsonResponse \ "name").asOpt[String].getOrElse(""),
          email = (jsonResponse \ "email").asOpt[String].getOrElse(""),
          accountType = "GITHUB",
          photoUrl = (jsonResponse \ "avatar_url").asOpt[String].getOrElse(""),
          location = (jsonResponse \ "location").asOpt[String].getOrElse(""),
          organization = (jsonResponse \ "company").asOpt[String].getOrElse(""),
          repoNumber = (jsonResponse \ "public_repos").as[Int],
          token = token,
          accountId = (jsonResponse \ "id").as[Int],
          owner = None
        )
    }
  }

}