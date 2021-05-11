package models.AuthorModel

import com.byteslounge.slickrepo.meta.{ Entity, Keyed }
import play.api.libs.json._

case class Author(override val id: Option[Long], name: String)
  extends Entity[Author, Long] {
  def withId(id: Long): Author = this.copy(id = Some(id))
}

object Author {
  implicit val _author = Json.format[Author]
}

import com.byteslounge.slickrepo.meta.Keyed
import com.byteslounge.slickrepo.repository.Repository
import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.ast.BaseTypedType
import slick.jdbc.JdbcProfile
import scala.concurrent.Future

@Singleton
class AuthorRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)
  extends Repository[Author, Long] {

  val driver = dbConfigProvider.get[JdbcProfile].profile
  import driver.api._
  val pkType = implicitly[BaseTypedType[Long]]
  val tableQuery = TableQuery[Authors]
  type TableType = Authors

  class Authors(tag: slick.lifted.Tag)
    extends Table[Author](tag, "Authors")
    with Keyed[Long] {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def * = (id.?, name) <> ((Author.apply _).tupled, Author.unapply)
  }

  def delete(id: Long): DBIO[Int] = {
    findOneCompiled(id).delete
  }

}
