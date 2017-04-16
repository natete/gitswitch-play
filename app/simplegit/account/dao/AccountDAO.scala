package simplegit.account.dao

import javax.inject.Inject

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import simplegit.account.model.Account
import slick.jdbc.JdbcProfile
import slick.jdbc.MySQLProfile.api._
import slick.lifted.{Index, ProvenShape}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by natete on 16/04/17.
  */
class AccountDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  private val accounts = TableQuery[Accounts]

  def getAccounts(userId: Long): Future[Seq[Account]] = db.run(accounts.filter(_.ownerId === userId).result)

  def create(account: Account): Future[Account] =
    db.run(accounts returning accounts.map(_.id) += account).map(newId => account.copy(id = Some(newId)))

  def delete(accountId: Long, userId: Long): Future[Unit] =
    db.run(accounts.filter(account => account.id === accountId && account.ownerId === userId).delete).map(_ => ())

  private class Accounts(tag: Tag) extends Table[Account](tag, "git_account") {
    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def username: Rep[String] = column[String]("username")

    def fullname: Rep[String] = column[String]("fullname")

    def email: Rep[String] = column[String]("email")

    def accountType: Rep[String] = column[String]("accountType")

    def photoUrl: Rep[String] = column[String]("photoUrl")

    def location: Rep[String] = column[String]("location")

    def organization: Rep[String] = column[String]("organization")

    def repoNumber: Rep[Int] = column[Int]("repoNumber")

    def token: Rep[String] = column[String]("token")

    def accountId: Rep[Int] = column[Int]("accountId")

    def ownerId: Rep[Long] = column[Long]("ownerId")

    def uq_username: Index = index("uq_username", (username, accountType), unique = true)

    def * : ProvenShape[Account] =
      (id.?,
        username,
        fullname,
        email,
        accountType,
        photoUrl,
        location,
        organization,
        repoNumber,
        token,
        accountId,
        ownerId.?) <> ((Account.apply _).tupled, Account.unapply)
  }

}
