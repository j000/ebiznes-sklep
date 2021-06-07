package models.OrderModel

import com.byteslounge.slickrepo.meta.{ Entity, Keyed }
import play.api.libs.json._

case class Order(override val id: Option[Long], user_id: Long)
  extends Entity[Order, Long] {
  def withId(id: Long): Order = this.copy(id = Some(id))
}

object Order {
  implicit val _order = Json.format[Order]
}

import com.byteslounge.slickrepo.meta.Keyed
import com.byteslounge.slickrepo.repository.Repository
import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.ast.BaseTypedType
import slick.jdbc.JdbcProfile
import scala.concurrent.Future

@Singleton
class OrderRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)
  extends Repository[Order, Long] {

  val driver = dbConfigProvider.get[JdbcProfile].profile
  import driver.api._
  val pkType = implicitly[BaseTypedType[Long]]
  val tableQuery = TableQuery[Orders]
  type TableType = Orders

  class Orders(tag: slick.lifted.Tag)
    extends Table[Order](tag, "Orders")
    with Keyed[Long] {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def user_id = column[Long]("user_id")
    def * = (id.?, user_id) <> ((Order.apply _).tupled, Order.unapply)
  }

  def delete(id: Long): DBIO[Int] = {
    findOneCompiled(id).delete
  }

}
