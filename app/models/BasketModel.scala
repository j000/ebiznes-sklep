package models.BasketModel

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

case class Basket(
  user: Long,
  book: Long,
  count: Long,
)

object Baskets extends EntityTableModule[Long, Basket]("Baskets") {
  class Row(tag: Tag) extends BaseEntRow(tag) with AutoNameSnakify {
    def user = column[Long]("user_id")
    def book = column[Long]("book_id")
    def count = column[Long]("count")
    def mapping = (user, book, count) <> ((Basket.apply _).tupled, Basket.unapply)
  }
}

@Singleton
class BasketRepository @Inject() (
  dbConfigProvider: DatabaseConfigProvider,
)(
  implicit ec: ExecutionContext,
) extends CRUDRepository[Basket](Baskets, dbConfigProvider)
{
  def update(id: Long, data: Basket): Future[Option[DBO]] = db run {
    Baskets.Q.filter(_.key === id).map(_.mapping) update (data) map {
      case 0 => None
      case _ => Some(SavedEntity[Long, Basket](id, data))
    }
  }
}

object Basket {
  implicit val _basket = Json.format[Basket]

  implicit val _writes: Writes[KeyedEntity[Long, Basket]] = (
    (JsPath \ "id").write[Long] and
    (JsPath).write[Basket]
  )(unlift(KeyedEntity.unapply[Long, Basket]))
  implicit val _reads: Reads[KeyedEntity[Long, Basket]] = (
    (JsPath \ "id").read[Long] and
    (JsPath).read[Basket]
  )(KeyedEntity.apply[Long, Basket] _)
  implicit val _format: Format[KeyedEntity[Long, Basket]] = Format(_reads, _writes)
}
