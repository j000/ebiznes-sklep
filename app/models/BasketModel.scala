package models.BasketModel

import com.byteslounge.slickrepo.meta.{ Entity, Keyed }
import play.api.libs.json._

case class Basket(
  override val id: Option[Long],
  user_id: Long,
  book_id: Long,
  count: Long,
) extends Entity[Basket, Long] {
  def withId(id: Long): Basket = this.copy(id = Some(id))
}

object Basket {
  implicit val _basket = Json.format[Basket]
}

import com.byteslounge.slickrepo.meta.Keyed
import com.byteslounge.slickrepo.repository.Repository
import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.ast.BaseTypedType
import slick.jdbc.JdbcProfile
import scala.concurrent.Future

@Singleton
class BasketRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)
  extends Repository[Basket, Long] {

  val driver = dbConfigProvider.get[JdbcProfile].profile
  import driver.api._
  val pkType = implicitly[BaseTypedType[Long]]
  val tableQuery = TableQuery[Baskets]
  type TableType = Baskets

  class Baskets(tag: slick.lifted.Tag)
    extends Table[Basket](tag, "Baskets")
    with Keyed[Long] {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def user_id = column[Long]("user_id")
    def book_id = column[Long]("book_id")
    def count = column[Long]("count")

    def * =
      (id.?, user_id, book_id, count) <>
        ((Basket.apply _).tupled, Basket.unapply)

  }

  def delete(id: Long): DBIO[Int] = {
    findOneCompiled(id).delete
  }

}
