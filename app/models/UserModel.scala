package models.UserModel

import com.byteslounge.slickrepo.meta.{ Entity, Keyed }
import play.api.libs.json._

case class User(
  override val id: Option[Long],
  nick: String,
) extends Entity[User, Long] {
  def withId(id: Long): User = this.copy(id = Some(id))
}

object User {
  implicit val _user = Json.format[User]
}

import com.byteslounge.slickrepo.meta.Keyed
import com.byteslounge.slickrepo.repository.Repository
import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import slick.ast.BaseTypedType
import slick.jdbc.JdbcProfile
import scala.concurrent.Future

class UserRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)
  extends Repository[User, Long] {

  val driver = dbConfigProvider.get[JdbcProfile].profile
  import driver.api._
  val pkType = implicitly[BaseTypedType[Long]]
  val tableQuery = TableQuery[Users]
  type TableType = Users

  class Users(tag: slick.lifted.Tag)
    extends Table[User](tag, "Users")
    with Keyed[Long] {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def nick = column[String]("nick")
    def * = (id.?, nick) <> ((User.apply _).tupled, User.unapply)
  }

  def delete(id: Long): DBIO[Int] = {
    findOneCompiled(id).delete
  }

}
