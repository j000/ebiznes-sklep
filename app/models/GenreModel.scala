package models.GenreModel

import scala.language.postfixOps
import models.SlickProfile.api._
import models.CRUDRepository
import slick.additions.entity._
import scala.concurrent.{ ExecutionContext, Future }
import javax.inject.{ Inject, Singleton }
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfig }
import slick.jdbc.JdbcProfile
import play.api.libs.json._
import play.api.libs.functional.syntax._

case class Genre(name: String)

object Genres extends EntityTableModule[Long, Genre]("Genres") {

  class Row(tag: Tag) extends BaseEntRow(tag) with AutoNameSnakify {
    def name = column[String]("name")
    def mapping = (name).mapTo[Genre]
  }

}

@Singleton
class GenreRepository @Inject() (
  dbConfigProvider: DatabaseConfigProvider,
)(
  implicit
  ec: ExecutionContext,
) extends CRUDRepository[Genre](Genres, dbConfigProvider) {

  def update(id: Long, data: Genre): Future[Option[DBO]] =
    db run {
      Genres.Q.filter(_.key === id).map(_.mapping) update
        (data) map {
          case 0 =>
            None
          case _ =>
            Some(SavedEntity[Long, Genre](id, data))
        }
    }

}

object Genre extends ((String) => Genre) {
  implicit val _genre = Json.format[Genre]

  implicit val _writes: Writes[KeyedEntity[Long, Genre]] =
    ((JsPath \ "id").write[Long] and (JsPath).write[Genre])(
      unlift(KeyedEntity.unapply[Long, Genre]),
    )

  implicit val _reads: Reads[KeyedEntity[Long, Genre]] =
    ((JsPath \ "id").read[Long] and (JsPath).read[Genre])(
      KeyedEntity.apply[Long, Genre] _,
    )

  implicit val _format: Format[KeyedEntity[Long, Genre]] = Format(
    _reads,
    _writes,
  )

}
