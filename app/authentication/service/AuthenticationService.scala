package authentication.service

import javax.inject.{Inject, Singleton}

import authentication.model.{JwtSecret, ResponseToken, TokenPayload}
import authentication.util.{HashUtils, JwtUtil}
import com.google.inject.ImplementedBy
import dao.UserDAO
import models.User
import org.joda.time.{DateTime, DateTimeZone}
import play.api.Configuration

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by natete on 15/04/17.
  */
@ImplementedBy(classOf[AuthenticationServiceImpl])
trait AuthenticationService {
  def authenticate(email: String, password: String): Future[(Option[ResponseToken])]

  def refreshToken(token: String): Future[Option[ResponseToken]]

  def validateToken(token: String): Future[Boolean]

  def getUserFromToken(token: String): Future[User]

  def getJwtSecret: JwtSecret
}

@Singleton()
class AuthenticationServiceImpl @Inject()(val userDAO: UserDAO,
                                          implicit val ec: ExecutionContext,
                                          val configuration: Configuration
                                         ) extends AuthenticationService {

  implicit private final val SECRET = JwtSecret(configuration.underlying.getString("jwt.token.secret"))
  private final val TOKEN_HOURS_TO_LIVE = configuration.underlying.getInt("jwt.token.hoursToLive")
  private final val AUTHORIZATION_TOKEN_VALID_START = "Bearer "

  override def authenticate(username: String, password: String): Future[(Option[ResponseToken])] = {
    for {
      user <- userDAO.findByUsername(username)
      result <- {
        if (isUserValidForAuthentication(user, password)) {
          Future.successful(Some(createAuthenticationToken(user.get)))
        } else {
          Future.successful(None)
        }
      }
    } yield result
  }

  override def refreshToken(token: String): Future[Option[ResponseToken]] = {
    JwtUtil.getPayloadIfValidToken[TokenPayload](token).flatMap {
      case Some(tokenPayload) =>
        for {
          user <- userDAO.findById(tokenPayload.userId)
        } yield Some(createAuthenticationToken(user.get))
      case None => Future.successful(None)
    }
  }

  override def validateToken(token: String): Future[Boolean] = {
    if (token.startsWith(AUTHORIZATION_TOKEN_VALID_START)) {
      JwtUtil.getPayloadIfValidToken[TokenPayload](token.replaceFirst(AUTHORIZATION_TOKEN_VALID_START, "")).map {
        case Some(tokenPayload) => tokenPayload.expiration.isBeforeNow
        case None => false
      }
    } else {
      Future.successful(false)
    }
  }

  override def getUserFromToken(token: String): Future[User] = {
    for {
      payloadCandidate <- JwtUtil.getPayloadIfValidToken[TokenPayload](token.replaceFirst(AUTHORIZATION_TOKEN_VALID_START, ""))
      user <- {
        payloadCandidate match {
          case Some(payload) => userDAO.findById(payload.userId)
          case None => throw new IllegalStateException(s"No available token")
        }
      }
    } yield user.get
  }

  override def getJwtSecret: JwtSecret = SECRET

  private def isUserValidForAuthentication(user: Option[User], password: String): Boolean = {
    user.nonEmpty && (user.get.shaPassword sameElements HashUtils.encodePassword(password))
  }

  private def createAuthenticationToken(user: User): ResponseToken = {
    val tokenPayload = TokenPayload(
      user.id.get,
      user.username,
      DateTime.now(DateTimeZone.UTC).plus(TOKEN_HOURS_TO_LIVE)
    )

    ResponseToken(JwtUtil.signJwtPayload(tokenPayload), TOKEN_HOURS_TO_LIVE * 3600)
  }
}
