package models.OrderModel

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

case class Order(
  user: Long,
)
case class Order_helper(
  order: Long,
  book: Long,
  price: Long,
  count: Long,
)

object Orders extends EntityTableModule[Long, Order]("Orders") {
  class Row(tag: Tag) extends BaseEntRow(tag) with AutoNameSnakify {
    def user = column[Long]("user_id")
    def mapping = (user).mapTo[Order]// <> ((Order.apply _).tupled, Order.unapply)
  }
}

object Orders_helper extends EntityTableModule[Long, Order_helper]("Orders_helper") {
  class Row(tag: Tag) extends BaseEntRow(tag) with AutoNameSnakify {
    def order = column[Long]("order_id")
    def book = column[Long]("book_id")
    def price = column[Long]("price")
    def count = column[Long]("count")
    def mapping = (order, book, price, count) <> ((Order_helper.apply _).tupled, Order_helper.unapply)
  }
}

@Singleton
class OrderRepository @Inject() (
  dbConfigProvider: DatabaseConfigProvider,
)(
  implicit ec: ExecutionContext,
) extends CRUDRepository[Order](Orders, dbConfigProvider)
{
  def update(id: Long, data: Order): Future[Option[DBO]] = db run {
    Orders.Q.filter(_.key === id).map(_.mapping) update (data) map {
      case 0 => None
      case _ => Some(SavedEntity[Long, Order](id, data))
    }
  }
}

object Order {
  implicit val _order = Json.format[Order]

  implicit val _writes: Writes[KeyedEntity[Long, Order]] = (
    (JsPath \ "id").write[Long] and
    (JsPath).write[Order]
  )(unlift(KeyedEntity.unapply[Long, Order]))
  implicit val _reads: Reads[KeyedEntity[Long, Order]] = (
    (JsPath \ "id").read[Long] and
    (JsPath).read[Order]
  )(KeyedEntity.apply[Long, Order] _)
  implicit val _format: Format[KeyedEntity[Long, Order]] = Format(_reads, _writes)
}

object Order_helper {
  implicit val _order = Json.format[Order_helper]

  implicit val _writes: Writes[KeyedEntity[Long, Order_helper]] = (
    (JsPath \ "id").write[Long] and
    (JsPath).write[Order_helper]
  )(unlift(KeyedEntity.unapply[Long, Order_helper]))
  implicit val _reads: Reads[KeyedEntity[Long, Order_helper]] = (
    (JsPath \ "id").read[Long] and
    (JsPath).read[Order_helper]
  )(KeyedEntity.apply[Long, Order_helper] _)
  implicit val _format: Format[KeyedEntity[Long, Order_helper]] = Format(_reads, _writes)
}
