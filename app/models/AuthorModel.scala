package models.AuthorModel

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

case class Author(name: String)

object Authors extends EntityTableModule[Long, Author]("Authors") {
  class Row(tag: Tag) extends BaseEntRow(tag) with AutoNameSnakify {
    def name = column[String]("name")
    def mapping = (name).mapTo[Author]
  }
}

@Singleton
class AuthorRepository @Inject() (
  dbConfigProvider: DatabaseConfigProvider,
)(
  implicit ec: ExecutionContext,
) extends CRUDRepository[Author](Authors, dbConfigProvider)
{
  def update(id: Long, data: Author): Future[Option[DBO]] = db run {
    Authors.Q.filter(_.key === id).map(_.mapping) update (data) map {
      case 0 => None
      case _ => Some(SavedEntity[Long, Author](id, data))
    }
  }
}

object Author extends ((String) => Author) {
  implicit val _author = Json.format[Author]

  implicit val _writes: Writes[KeyedEntity[Long, Author]] = (
    (JsPath \ "id").write[Long] and
    (JsPath).write[Author]
  )(unlift(KeyedEntity.unapply[Long, Author]))
  implicit val _reads: Reads[KeyedEntity[Long, Author]] = (
    (JsPath \ "id").read[Long] and
    (JsPath).read[Author]
  )(KeyedEntity.apply[Long, Author] _)
  implicit val _format: Format[KeyedEntity[Long, Author]] = Format(_reads, _writes)
}
