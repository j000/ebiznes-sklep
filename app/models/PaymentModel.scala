package models.PaymentModel

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

case class Payment(order: Long, amount: Long, comment: Option[String])

object Payments extends EntityTableModule[Long, Payment]("Payments") {

  class Row(tag: Tag) extends BaseEntRow(tag) with AutoNameSnakify {
    def order = column[Long]("order_id")
    def amount = column[Long]("amount")
    def comment = column[Option[String]]("comment")

    def mapping =
      (order, amount, comment) <> ((Payment.apply _).tupled, Payment.unapply)

  }

}

@Singleton
class PaymentRepository @Inject() (
  dbConfigProvider: DatabaseConfigProvider,
)(
  implicit
  ec: ExecutionContext,
) extends CRUDRepository[Payment](Payments, dbConfigProvider) {

  def update(id: Long, data: Payment): Future[Option[DBO]] =
    db run {
      Payments.Q.filter(_.key === id).map(_.mapping) update
        (data) map {
          case 0 =>
            None
          case _ =>
            Some(SavedEntity[Long, Payment](id, data))
        }
    }

}

object Payment {
  implicit val _payment = Json.format[Payment]

  implicit val _writes: Writes[KeyedEntity[Long, Payment]] =
    ((JsPath \ "id").write[Long] and (JsPath).write[Payment])(
      unlift(KeyedEntity.unapply[Long, Payment]),
    )

  implicit val _reads: Reads[KeyedEntity[Long, Payment]] =
    ((JsPath \ "id").read[Long] and (JsPath).read[Payment])(
      KeyedEntity.apply[Long, Payment] _,
    )

  implicit val _format: Format[KeyedEntity[Long, Payment]] = Format(
    _reads,
    _writes,
  )

}
