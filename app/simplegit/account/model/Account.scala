package simplegit.account.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import play.api.libs.json._

/**
  * Created by natete on 16/04/17.
  */
@JsonIgnoreProperties(Array("token"))
case class Account(id: Option[Long],
                   username: String,
                   fullname: String,
                   email: String,
                   accountType: String,
                   photoUrl: String,
                   location: String,
                   organization: String,
                   repoNumber: Int,
                   token: String,
                   accountId: Int,
                   owner: Option[Long]
                  )

object Account {
  implicit val accountReads: Reads[Account] = Json.reads[Account]

  implicit val accountWrites: Writes[Account] = Writes { account =>
    Json.obj(
      "id" -> account.id,
      "username" -> account.username,
      "fullname" -> account.fullname,
      "email" -> account.email,
      "type" -> account.accountType,
      "photoUrl" -> account.photoUrl,
      "location" -> account.location,
      "organization" -> account.organization,
      "repoNumber" -> account.repoNumber,
      "accountId" -> account.accountId
    )
  }

}
