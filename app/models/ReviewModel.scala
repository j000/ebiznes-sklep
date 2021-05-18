package models.ReviewModel

import com.byteslounge.slickrepo.meta.{ Entity, Keyed }
import play.api.libs.json._

case class Review(
  override val id: Option[Long],
  content: String,
  user_id: Long,
  book_id: Long,
) extends Entity[Review, Long] {
  def withId(id: Long): Review = this.copy(id = Some(id))
}

object Review {
  implicit val _review = Json.format[Review]
}

import com.byteslounge.slickrepo.meta.Keyed
import com.byteslounge.slickrepo.repository.Repository
import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.ast.BaseTypedType
import slick.jdbc.JdbcProfile
import scala.concurrent.Future

@Singleton
class ReviewRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)
  extends Repository[Review, Long] {

  val driver = dbConfigProvider.get[JdbcProfile].profile
  import driver.api._
  val pkType = implicitly[BaseTypedType[Long]]
  val tableQuery = TableQuery[Reviews]
  type TableType = Reviews

  class Reviews(tag: slick.lifted.Tag)
    extends Table[Review](tag, "Reviews")
    with Keyed[Long] {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def content = column[String]("content")
    def user_id = column[Long]("user_id")
    def book_id = column[Long]("book_id")
    def * = (id.?, content, user_id, book_id) <> ((Review.apply _).tupled, Review.unapply)
  }

  def delete(id: Long): DBIO[Int] = {
    findOneCompiled(id).delete
  }

}
