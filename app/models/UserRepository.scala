package models

import scala.language.postfixOps
import SlickProfile.api._
import slick.additions.entity._
import scala.concurrent.{ Future, ExecutionContext }
import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import play.api.libs.json._
import play.api.libs.functional.syntax._

case class User(login: String, password: String)

private object Users extends EntityTableModule[Long, User]("Users") {
  class Row(tag: Tag) extends BaseEntRow(tag) with AutoNameSnakify {
    val login = col[String]
    val password = col[String]
    def mapping = (login, password).mapTo[User]
  }
}

@Singleton
class UserRepository @Inject() (
  dbConfigProvider: DatabaseConfigProvider
)(
  implicit ec: ExecutionContext
){
  type UserDBO = KeyedEntity[Long, User]

  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  def index(): Future[Seq[UserDBO]] = db run {
    Users.Q result
  }

  def create(user: User): Future[UserDBO] = db run {
    Users.Q insert user
  }

  def read(id: Long): Future[Option[UserDBO]] = db run {
    // TODO how to skip dots here?
    Users.Q.filter(_.key === id).result.headOption
  }

  def update(id: Long, user: User): Future[Option[UserDBO]] = db run {
    Users.Q.filter(_.key === id).map(_.mapping) update (user) map {
      case 0 => None
      case _ => Some(SavedEntity[Long, User](id, user))
    }
  }

  def update(user: UserDBO): Future[Option[UserDBO]] = db run {
    Users.Q insert user map { Some(_) }
  }

  def delete(id: Long): Future[Int] = db run {
    Users.Q.filter(_.key === id) delete
  }
}

object UserJson {
  type UserDBO = KeyedEntity[Long, User]

  implicit val _user = Json.format[User]

  implicit val _writes: Writes[UserDBO] = (
    (JsPath \ "id").write[Long] and
    (JsPath).write[User]
  )(unlift(KeyedEntity.unapply[Long, User]))
  implicit val _reads: Reads[UserDBO] = (
    (JsPath \ "id").read[Long] and
    (JsPath).read[User]
  )(KeyedEntity.apply[Long, User] _)
  implicit val _format: Format[UserDBO] = Format(_reads, _writes)
}
