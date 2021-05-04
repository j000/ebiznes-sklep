package models.FavouriteModel

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

case class Favourite(
  user: Long,
  book: Option[Long],
  author: Option[Long],
  genre: Option[Long],
)

object Favourites extends EntityTableModule[Long, Favourite]("Favourites") {
  class Row(tag: Tag) extends BaseEntRow(tag) with AutoNameSnakify {
    def user = column[Long]("user_id")
    def book = column[Option[Long]]("book_id")
    def author = column[Option[Long]]("author_id")
    def genre = column[Option[Long]]("genre_id")
    def mapping = (user, book, author, genre) <> ((Favourite.apply _).tupled, Favourite.unapply) //.mapTo[Favourite]
  }
}

@Singleton
class FavouriteRepository @Inject() (
  dbConfigProvider: DatabaseConfigProvider,
)(
  implicit ec: ExecutionContext,
) extends CRUDRepository[Favourite](Favourites, dbConfigProvider)
{
  def update(id: Long, data: Favourite): Future[Option[DBO]] = db run {
    Favourites.Q.filter(_.key === id).map(_.mapping) update (data) map {
      case 0 => None
      case _ => Some(SavedEntity[Long, Favourite](id, data))
    }
  }
}

object Favourite {
  implicit val _favourite = Json.format[Favourite]

  implicit val _writes: Writes[KeyedEntity[Long, Favourite]] = (
    (JsPath \ "id").write[Long] and
    (JsPath).write[Favourite]
  )(unlift(KeyedEntity.unapply[Long, Favourite]))
  implicit val _reads: Reads[KeyedEntity[Long, Favourite]] = (
    (JsPath \ "id").read[Long] and
    (JsPath).read[Favourite]
  )(KeyedEntity.apply[Long, Favourite] _)
  implicit val _format: Format[KeyedEntity[Long, Favourite]] = Format(_reads, _writes)
}
