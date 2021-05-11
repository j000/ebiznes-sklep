package models.GenreModel

import com.byteslounge.slickrepo.meta.{ Entity, Keyed }
import play.api.libs.json._

case class Genre(override val id: Option[Long], name: String)
  extends Entity[Genre, Long] {
  def withId(id: Long): Genre = this.copy(id = Some(id))
}

object Genre {
  implicit val _genre = Json.format[Genre]
}

import com.byteslounge.slickrepo.meta.Keyed
import com.byteslounge.slickrepo.repository.Repository
import javax.inject._
import play.api.db.slick.DatabaseConfigProvider
import slick.ast.BaseTypedType
import slick.jdbc.JdbcProfile
import scala.concurrent.Future

class GenreRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)
  extends Repository[Genre, Long] {

  val driver = dbConfigProvider.get[JdbcProfile].profile
  import driver.api._
  val pkType = implicitly[BaseTypedType[Long]]
  val tableQuery = TableQuery[Genres]
  type TableType = Genres

  class Genres(tag: slick.lifted.Tag)
    extends Table[Genre](tag, "Genres")
    with Keyed[Long] {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def * = (id.?, name) <> ((Genre.apply _).tupled, Genre.unapply)
  }

  def delete(id: Long): DBIO[Int] = {
    findOneCompiled(id).delete
  }

}
