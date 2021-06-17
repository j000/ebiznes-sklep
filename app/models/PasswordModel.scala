package models.PasswordModel

import com.byteslounge.slickrepo.meta.{ Entity, Keyed }
import play.api.libs.json._
import com.mohiva.play.silhouette.api.{ Identity, LoginInfo }
import com.mohiva.play.silhouette.api.services.IdentityService

case class Password(
  override val id: Option[Long],
  providerID: Long,
  hasher: String,
  password: String,
  salt: Option[String],
) extends Entity[Password, Long] {
  def withId(id: Long): Password = this.copy(id = Some(id))
}

object Password {
  implicit val _password = Json.format[Password]
}

import com.byteslounge.slickrepo.meta.Keyed
import com.byteslounge.slickrepo.repository.Repository
import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.ast.BaseTypedType
import slick.jdbc.JdbcProfile
import scala.concurrent.Future
import utils.DBImplicits

class PasswordRepository @Inject() (
  dbConfigProvider: DatabaseConfigProvider,
  dbExecutor: DBImplicits,
) extends Repository[Password, Long] {
  import dbExecutor.executeOperation

  val driver = dbConfigProvider.get[JdbcProfile].profile
  import driver.api._
  val pkType = implicitly[BaseTypedType[Long]]
  val tableQuery = TableQuery[Passwords]
  type TableType = Passwords

  class Passwords(tag: slick.lifted.Tag)
    extends Table[Password](tag, "Passwords")
    with Keyed[Long] {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def providerID = column[Long]("provider_id")
    def hasher = column[String]("hasher")
    def password = column[String]("password")
    def salt = column[Option[String]]("salt")

    def * =
      (id.?, providerID, hasher, password, salt) <>
        ((Password.apply _).tupled, Password.unapply)

  }

  def delete(id: Long): DBIO[Int] = {
    findOneCompiled(id).delete
  }

}
