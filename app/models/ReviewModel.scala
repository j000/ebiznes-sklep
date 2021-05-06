package models.ReviewModel

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

case class Review(content: String, user: Long, book: Long)

object Reviews extends EntityTableModule[Long, Review]("Reviews") {

  class Row(tag: Tag) extends BaseEntRow(tag) with AutoNameSnakify {
    def content = column[String]("content")
    def user = column[Long]("user_id")
    def book = column[Long]("book_id")
    def mapping = (content, user, book).mapTo[Review]
  }

}

@Singleton
class ReviewRepository @Inject() (
  dbConfigProvider: DatabaseConfigProvider,
)(
  implicit
  ec: ExecutionContext,
) extends CRUDRepository[Review](Reviews, dbConfigProvider) {

  def update(id: Long, data: Review): Future[Option[DBO]] =
    db run {
      Reviews.Q.filter(_.key === id).map(_.mapping) update
        (data) map {
          case 0 =>
            None
          case _ =>
            Some(SavedEntity[Long, Review](id, data))
        }
    }

}

object Review extends ((String, Long, Long) => Review) {
  implicit val _review = Json.format[Review]

  implicit val _writes: Writes[KeyedEntity[Long, Review]] =
    ((JsPath \ "id").write[Long] and (JsPath).write[Review])(
      unlift(KeyedEntity.unapply[Long, Review]),
    )

  implicit val _reads: Reads[KeyedEntity[Long, Review]] =
    ((JsPath \ "id").read[Long] and (JsPath).read[Review])(
      KeyedEntity.apply[Long, Review] _,
    )

  implicit val _format: Format[KeyedEntity[Long, Review]] = Format(
    _reads,
    _writes,
  )

}
