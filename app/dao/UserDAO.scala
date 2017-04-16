package dao

import javax.inject.Inject

import authentication.util.HashUtils
import models.User
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.jdbc.JdbcProfile
import slick.jdbc.MySQLProfile.api._
import slick.lifted.ProvenShape

import scala.concurrent.Future

/**
  * Created by natete on 15/04/17.
  */
class UserDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  private val users = TableQuery[Users]

  def create(username: String, password: String): Future[Unit] = {
    val user = new User(None, username, HashUtils.encodePassword(password))
    db.run(users += user).map(_ => ())
  }

  def findByUsername(username: String): Future[Option[User]] = {
    db.run(users.filter(_.username === username).result.headOption)
  }

  def findById(userId: Long): Future[Option[User]] = {
    db.run(users.filter(_.id === userId).result.headOption)
  }

  private class Users(tag: Tag) extends Table[User](tag, "user_account") {

    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def username: Rep[String] = column[String]("username")

    def shaPassword: Rep[Array[Byte]] = column[Array[Byte]]("sha_password")

    def * : ProvenShape[User] = (id.?, username, shaPassword) <> ((User.apply _).tupled, User.unapply)
  }

}
