package models.PaymentModel

import com.byteslounge.slickrepo.meta.{ Entity, Keyed }
import play.api.libs.json._

case class Payment(
  override val id: Option[Long],
  order_id: Long,
  amount: Long,
  comment: Option[String],
) extends Entity[Payment, Long] {
  def withId(id: Long): Payment = this.copy(id = Some(id))
}

object Payment {
  implicit val _payment = Json.format[Payment]
}

import com.byteslounge.slickrepo.meta.Keyed
import com.byteslounge.slickrepo.repository.Repository
import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.ast.BaseTypedType
import slick.jdbc.JdbcProfile
import scala.concurrent.Future

@Singleton
class PaymentRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)
  extends Repository[Payment, Long] {

  val driver = dbConfigProvider.get[JdbcProfile].profile
  import driver.api._
  val pkType = implicitly[BaseTypedType[Long]]
  val tableQuery = TableQuery[Payments]
  type TableType = Payments

  class Payments(tag: slick.lifted.Tag)
    extends Table[Payment](tag, "Payments")
    with Keyed[Long] {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def order_id = column[Long]("order_id")
    def amount = column[Long]("amount")
    def comment = column[Option[String]]("comment")

    def * =
      (id.?, order_id, amount, comment) <>
        ((Payment.apply _).tupled, Payment.unapply)

  }

  def delete(id: Long): DBIO[Int] = {
    findOneCompiled(id).delete
  }

}
