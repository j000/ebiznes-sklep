package models.UserModel

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

case class User(login: String, password: String)

object Users extends EntityTableModule[Long, User]("Users") {

  class Row(tag: Tag) extends BaseEntRow(tag) with AutoNameSnakify {
    val login = col[String]
    val password = col[String]
    def mapping = (login, password).mapTo[User]
  }

}

@Singleton
class UserRepository @Inject() (
  dbConfigProvider: DatabaseConfigProvider,
)(
  implicit
  ec: ExecutionContext,
) extends CRUDRepository[User](Users, dbConfigProvider) {

  def update(id: Long, data: User): Future[Option[DBO]] =
    db run {
      Users.Q.filter(_.key === id).map(_.mapping) update
        (data) map {
          case 0 =>
            None
          case _ =>
            Some(SavedEntity[Long, User](id, data))
        }
    }

}

object User extends ((String, String) => User) {
  implicit val _user = Json.format[User]

  implicit val _writes: Writes[KeyedEntity[Long, User]] =
    ((JsPath \ "id").write[Long] and (JsPath).write[User])(
      unlift(KeyedEntity.unapply[Long, User]),
    )

  implicit val _reads: Reads[KeyedEntity[Long, User]] =
    ((JsPath \ "id").read[Long] and (JsPath).read[User])(
      KeyedEntity.apply[Long, User] _,
    )

  implicit val _format: Format[KeyedEntity[Long, User]] = Format(
    _reads,
    _writes,
  )

}
