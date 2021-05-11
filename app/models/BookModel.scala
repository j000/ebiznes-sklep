package models.BookModel

import com.byteslounge.slickrepo.meta.{ Entity, Keyed }
import play.api.libs.json._

case class Book(
  override val id: Option[Long],
  title: String,
  author_id: Long,
  genre_id: Long,
  price: Long,
) extends Entity[Book, Long] {
  def withId(id: Long): Book = this.copy(id = Some(id))
}

object Book {
  implicit val _book = Json.format[Book]
}

import com.byteslounge.slickrepo.meta.Keyed
import com.byteslounge.slickrepo.repository.Repository
import javax.inject._
import play.api.db.slick.DatabaseConfigProvider
import slick.ast.BaseTypedType
import slick.jdbc.JdbcProfile
import scala.concurrent.Future

@Singleton
class BookRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)
  extends Repository[Book, Long] {

  val driver = dbConfigProvider.get[JdbcProfile].profile
  import driver.api._
  val pkType = implicitly[BaseTypedType[Long]]
  val tableQuery = TableQuery[Books]
  type TableType = Books

  class Books(tag: slick.lifted.Tag)
    extends Table[Book](tag, "Books")
    with Keyed[Long] {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def title = column[String]("title")
    def author_id = column[Long]("author_id")
    def genre_id = column[Long]("genre_id")
    def price = column[Long]("price")

    def * =
      (id.?, title, author_id, genre_id, price) <>
        ((Book.apply _).tupled, Book.unapply)

  }

  def delete(id: Long): DBIO[Int] = {
    findOneCompiled(id).delete
  }

}
