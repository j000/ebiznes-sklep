package models

import scala.language.postfixOps
import models.SlickProfile.api._
import slick.additions.entity._
import scala.concurrent.{ Future, ExecutionContext }
import javax.inject.{ Inject, Singleton }
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.jdbc.JdbcProfile
import play.api.libs.json._
import play.api.libs.functional.syntax._

class CRUDRepository[T] @Inject() (
  table: EntityTableModule[Long, T],
  protected val dbConfigProvider: DatabaseConfigProvider,
)(
  implicit ec: ExecutionContext,
) extends HasDatabaseConfig[JdbcProfile] {
  type DBO = KeyedEntity[Long, T]

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  def index(): Future[Seq[DBO]] = db run {
    table.Q result
  }

  def create(user: T): Future[DBO] = db run {
    table.Q insert user
  }

  def read(id: Long): Future[Option[DBO]] = db run {
    // TODO how to skip dots here?
    table.Q.filter(_.key === id).result.headOption
  }

  // W T F
  // def update(id: Long, data: T): Future[Option[DBO]] = db run {
  //   table.Q.filter(_.key === id).map(_.mapping) update (data) map {
  //     case 0 => None
  //     case _ => Some(SavedEntity[Long, T](id, data))
  //   }
  // }
  //
  def update(user: DBO): Future[Option[DBO]] = db run {
    table.Q insert user map { Some(_) }
  }

  def delete(id: Long): Future[Int] = db run {
    table.Q.filter(_.key === id) delete
  }
}
