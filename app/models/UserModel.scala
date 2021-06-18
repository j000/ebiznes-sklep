package models.UserModel

import com.mohiva.play.silhouette.api.{ Identity, LoginInfo }
import com.mohiva.play.silhouette.api.services.IdentityService
import com.byteslounge.slickrepo.meta.{ Entity, Keyed }
import play.api.libs.json._

case class User(
  override val id: Option[Long],
  nick: String,
  providerId: String,
  providerKey: String,
  email: String,
) extends Entity[User, Long]
  with Identity {
  def withId(id: Long): User = this.copy(id = Some(id))
}

object User {
  implicit val _user = Json.format[User]
}

import com.byteslounge.slickrepo.meta.Keyed
import com.byteslounge.slickrepo.repository.Repository
import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import slick.ast.BaseTypedType
import slick.jdbc.JdbcProfile
import scala.concurrent.Future
import utils.DBImplicits

class UserRepository @Inject() (
  dbConfigProvider: DatabaseConfigProvider,
  dbExecutor: DBImplicits,
) extends Repository[User, Long]
  with IdentityService[User] {
  import dbExecutor.executeOperation

  val driver = dbConfigProvider.get[JdbcProfile].profile
  import driver.api._
  val pkType = implicitly[BaseTypedType[Long]]
  val tableQuery = TableQuery[Users]
  type TableType = Users

  class Users(tag: slick.lifted.Tag)
    extends Table[User](tag, "Users")
    with Keyed[Long] {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def nick = column[String]("nick")
    def providerId = column[String]("provider_id")
    def providerKey = column[String]("provider_key")
    def email = column[String]("email")

    def * =
      (id.?, nick, providerId, providerKey, email) <>
        ((User.apply _).tupled, User.unapply)

  }

  def delete(id: Long): DBIO[Int] = {
    findOneCompiled(id).delete
  }

  override def retrieve(loginInfo: LoginInfo): Future[Option[User]] = {
    val tmp =
      tableQuery
        .filter(u =>
          u.providerId === loginInfo.providerID &&
            u.providerKey === loginInfo.providerKey,
        )
        .result
        .headOption
    return tmp
  }

}
