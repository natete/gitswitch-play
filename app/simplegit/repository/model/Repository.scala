package simplegit.repository.model

import play.api.libs.json.{Format, Json}

/**
  * Created by natete on 17/04/17.
  */
case class Repository(id: Long,
                      name: String,
                      repoType: String,
                      username: String,
                      accountId: Long,
                      canAdmin: Boolean,
                      age: String,
                      updated: String,
                      language: String,
                      issues: Int
                     )

object Repository {
  implicit val jsonFormat: Format[Repository] = Json.format[Repository]
}
