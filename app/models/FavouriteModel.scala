package models.FavouriteModel

import com.byteslounge.slickrepo.meta.{ Entity, Keyed }
import play.api.libs.json._

case class Favourite(
  override val id: Option[Long],
  user_id: Long,
  book_id: Option[Long],
  author_id: Option[Long],
  genre_id: Option[Long],
) extends Entity[Favourite, Long] {
  def withId(id: Long): Favourite = this.copy(id = Some(id))
}

object Favourite {
  implicit val _favourite = Json.format[Favourite]
}

import com.byteslounge.slickrepo.meta.Keyed
import com.byteslounge.slickrepo.repository.Repository
import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.ast.BaseTypedType
import slick.jdbc.JdbcProfile
import scala.concurrent.Future

@Singleton
class FavouriteRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)
  extends Repository[Favourite, Long] {

  val driver = dbConfigProvider.get[JdbcProfile].profile
  import driver.api._
  val pkType = implicitly[BaseTypedType[Long]]
  val tableQuery = TableQuery[Favourites]
  type TableType = Favourites

  class Favourites(tag: slick.lifted.Tag)
    extends Table[Favourite](tag, "Favourites")
    with Keyed[Long] {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def user_id = column[Long]("user_id")
    def book_id = column[Option[Long]]("book_id")
    def author_id = column[Option[Long]]("author_id")
    def genre_id = column[Option[Long]]("genre_id")

    def * =
      (id.?, user_id, book_id, author_id, genre_id) <>
        ((Favourite.apply _).tupled, Favourite.unapply)

  }

  def delete(id: Long): DBIO[Int] = {
    findOneCompiled(id).delete
  }

}
