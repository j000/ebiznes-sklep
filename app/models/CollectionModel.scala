package models.CollectionModel

import com.byteslounge.slickrepo.meta.{ Entity, Keyed }
import play.api.libs.json._

case class Collection(override val id: Option[Long], name: String)
  extends Entity[Collection, Long] {
  def withId(id: Long): Collection = this.copy(id = Some(id))
}

object Collection {
  implicit val _collection = Json.format[Collection]
}

import com.byteslounge.slickrepo.meta.Keyed
import com.byteslounge.slickrepo.repository.Repository
import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.ast.BaseTypedType
import slick.jdbc.JdbcProfile
import scala.concurrent.Future

@Singleton
class CollectionRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)
  extends Repository[Collection, Long] {

  val driver = dbConfigProvider.get[JdbcProfile].profile
  import driver.api._
  val pkType = implicitly[BaseTypedType[Long]]
  val tableQuery = TableQuery[Collections]
  type TableType = Collections

  class Collections(tag: slick.lifted.Tag)
    extends Table[Collection](tag, "Collections")
    with Keyed[Long] {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def * = (id.?, name) <> ((Collection.apply _).tupled, Collection.unapply)
  }

  def delete(id: Long): DBIO[Int] = {
    findOneCompiled(id).delete
  }

}
