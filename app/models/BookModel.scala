package models.BookModel

import scala.language.postfixOps
import models.SlickProfile.api._
import models.CRUDRepository
import slick.additions.entity._
import scala.concurrent.{ Future, ExecutionContext }
import javax.inject.{ Inject, Singleton }
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.jdbc.JdbcProfile
import play.api.libs.json._
import play.api.libs.functional.syntax._

import models.AuthorModel.Authors
import models.GenreModel.Genres

case class Book(
  title: String,
  author_id: Long,
  genre_id: Long,
  price: Long,
)

object Books extends EntityTableModule[Long, Book]("Books") {
  class Row(tag: Tag) extends BaseEntRow(tag) with AutoNameSnakify {
    def title = column[String]("title")
    def author_id = column[Long]("author_id")
    def genre_id = column[Long]("genre_id")
    def price = column[Long]("price")
    def mapping = (title, author_id, genre_id, price).mapTo[Book]
  }
}

@Singleton
class BookRepository @Inject() (
  dbConfigProvider: DatabaseConfigProvider,
)(
  implicit ec: ExecutionContext,
) extends CRUDRepository[Book](Books, dbConfigProvider)
{
  def update(id: Long, data: Book): Future[Option[DBO]] = db run {
    Books.Q.filter(_.key === id).map(_.mapping) update (data) map {
      case 0 => None
      case _ => Some(SavedEntity[Long, Book](id, data))
    }
  }
}

object Book extends ((
  String,
  Long,
  Long,
  Long,
) => Book) {
  implicit val _book = Json.format[Book]

  implicit val _writes: Writes[KeyedEntity[Long, Book]] = (
    (JsPath \ "id").write[Long] and
    (JsPath).write[Book]
  )(unlift(KeyedEntity.unapply[Long, Book]))
  implicit val _reads: Reads[KeyedEntity[Long, Book]] = (
    (JsPath \ "id").read[Long] and
    (JsPath).read[Book]
  )(KeyedEntity.apply[Long, Book] _)
  implicit val _format: Format[KeyedEntity[Long, Book]] = Format(_reads, _writes)
}
