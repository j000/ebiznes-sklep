package models.CollectionModel

import com.byteslounge.slickrepo.meta.{ Entity, Keyed }
import play.api.libs.json._
import models.BookModel.Book

case class Collection(override val id: Option[Long], name: String, books: Seq[Book] = Seq.empty)
  extends Entity[Collection, Long] {
  def withId(id: Long): Collection = this.copy(id = Some(id))

  def loadBooks(books: Seq[Book]): Collection = this.copy(books=books)
}

object Collection {
  implicit val _collection = Json.format[Collection]
}

case class CollectionHelper(override val id: Option[Long], book_id: Long, collection_id: Long)
  extends Entity[CollectionHelper, Long] {
  def withId(id: Long): CollectionHelper = this.copy(id = Some(id))
}

import com.byteslounge.slickrepo.meta.Keyed
import com.byteslounge.slickrepo.repository.Repository
import javax.inject._
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
    def * = (id.?, name) <> (
      { case (id, name) => Collection(id, name) },
      {(_: Collection) match { case Collection(id, name, _) => Some(id, name) }}
    )
      // ((Collection.apply _).tupled, Collection.unapply)
  }

  def delete(id: Long): DBIO[Int] = {
    findOneCompiled(id).delete
  }

}

@Singleton
class CollectionHelperRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)
  extends Repository[CollectionHelper, Long] {

  val driver = dbConfigProvider.get[JdbcProfile].profile
  import driver.api._
  val pkType = implicitly[BaseTypedType[Long]]
  type TableType = CollectionHelpers
  val tableQuery = TableQuery[TableType]

  class CollectionHelpers(tag: slick.lifted.Tag)
    extends Table[CollectionHelper](tag, "Collections")
    with Keyed[Long] {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def collection_id = column[Long]("collection_id")
    def book_id = column[Long]("book_id")
    def * = (id.?, collection_id, book_id) <> ((CollectionHelper.apply _).tupled, CollectionHelper.unapply)

    // def book = foreignKey("book_fk", book_id, BookRepository.TableType)(_.id)
  }

  def delete(id: Long): DBIO[Int] = {
    findOneCompiled(id).delete
  }

}
