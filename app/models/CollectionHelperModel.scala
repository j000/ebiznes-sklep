package models.CollectionHelperModel

import com.byteslounge.slickrepo.meta.{ Entity, Keyed }
import play.api.libs.json._

case class CollectionHelper(
  override val id: Option[Long],
  collection_id: Long,
  book_id: Long,
) extends Entity[CollectionHelper, Long] {
  def withId(id: Long): CollectionHelper = this.copy(id = Some(id))
}

object CollectionHelper {
  implicit val _collectionhelper = Json.format[CollectionHelper]
}

import com.byteslounge.slickrepo.meta.Keyed
import com.byteslounge.slickrepo.repository.Repository
import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.ast.BaseTypedType
import slick.jdbc.JdbcProfile
import scala.concurrent.Future

@Singleton
class CollectionHelperRepository @Inject() (
  dbConfigProvider: DatabaseConfigProvider,
) extends Repository[CollectionHelper, Long] {

  val driver = dbConfigProvider.get[JdbcProfile].profile
  import driver.api._
  val pkType = implicitly[BaseTypedType[Long]]
  val tableQuery = TableQuery[CollectionHelpers]
  type TableType = CollectionHelpers

  class CollectionHelpers(tag: slick.lifted.Tag)
    extends Table[CollectionHelper](tag, "CollectionHelpers")
    with Keyed[Long] {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def collection_id = column[Long]("collection_id")
    def book_id = column[Long]("book_id")

    def * =
      (id.?, collection_id, book_id) <>
        ((CollectionHelper.apply _).tupled, CollectionHelper.unapply)

  }

  def delete(id: Long): DBIO[Int] = {
    findOneCompiled(id).delete
  }

}
