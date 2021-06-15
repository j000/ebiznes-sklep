package models.ProviderModel

import com.byteslounge.slickrepo.meta.{ Entity, Keyed }
import play.api.libs.json._
import com.mohiva.play.silhouette.api.{ Identity, LoginInfo }
import com.mohiva.play.silhouette.api.services.IdentityService

case class Provider(
  override val id: Option[Long],
  userID: Long,
  providerID: String,
  providerKey: String,
) extends Entity[Provider, Long] {
  def withId(id: Long): Provider = this.copy(id = Some(id))
}

object Provider {
  implicit val _provider = Json.format[Provider]
}

import com.byteslounge.slickrepo.meta.Keyed
import com.byteslounge.slickrepo.repository.Repository
import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.ast.BaseTypedType
import slick.jdbc.JdbcProfile
import scala.concurrent.Future
import utils.DBImplicits

class ProviderRepository @Inject() (
  dbConfigProvider: DatabaseConfigProvider,
  dbExecutor: DBImplicits,
) extends Repository[Provider, Long] {
  import dbExecutor.executeOperation

  val driver = dbConfigProvider.get[JdbcProfile].profile
  import driver.api._
  val pkType = implicitly[BaseTypedType[Long]]
  val tableQuery = TableQuery[Providers]
  type TableType = Providers

  class Providers(tag: slick.lifted.Tag)
    extends Table[Provider](tag, "Providers")
    with Keyed[Long] {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def userID = column[Long]("user_id")
    def providerID = column[String]("provider_id")
    def providerKey = column[String]("provider_key")

    def * =
      (id.?, userID, providerID, providerKey) <>
        ((Provider.apply _).tupled, Provider.unapply)

  }

  def delete(id: Long): DBIO[Int] = {
    findOneCompiled(id).delete
  }
}
