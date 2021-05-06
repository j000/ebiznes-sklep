package models.CollectionModel

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

case class Collection(name: String)
case class Collection_helper(collection: Long, book: Long)

object Collections extends EntityTableModule[Long, Collection]("Collections") {

  class Row(tag: Tag) extends BaseEntRow(tag) with AutoNameSnakify {
    def name = column[String]("name")
    def mapping = (name).mapTo[Collection]
  }

}

object Collections_helper
  extends EntityTableModule[Long, Collection_helper]("Collections_helper") {

  class Row(tag: Tag) extends BaseEntRow(tag) with AutoNameSnakify {
    def collection = column[Long]("collection_id")
    def book = column[Long]("book_id")
    def mapping = (collection, book).mapTo[Collection_helper]
  }

}

@Singleton
class CollectionRepository @Inject() (
  dbConfigProvider: DatabaseConfigProvider,
)(
  implicit
  ec: ExecutionContext,
) extends CRUDRepository[Collection](Collections, dbConfigProvider) {

  def update(id: Long, data: Collection): Future[Option[DBO]] =
    db run {
      Collections.Q.filter(_.key === id).map(_.mapping) update
        (data) map {
          case 0 =>
            None
          case _ =>
            Some(SavedEntity[Long, Collection](id, data))
        }
    }

}

object Collection extends ((String) => Collection) {
  implicit val _collection = Json.format[Collection]

  implicit val _writes: Writes[KeyedEntity[Long, Collection]] =
    ((JsPath \ "id").write[Long] and (JsPath).write[Collection])(
      unlift(KeyedEntity.unapply[Long, Collection]),
    )

  implicit val _reads: Reads[KeyedEntity[Long, Collection]] =
    ((JsPath \ "id").read[Long] and (JsPath).read[Collection])(
      KeyedEntity.apply[Long, Collection] _,
    )

  implicit val _format: Format[KeyedEntity[Long, Collection]] = Format(
    _reads,
    _writes,
  )

}

object Collection_helper extends ((Long, Long) => Collection_helper) {
  implicit val _collection = Json.format[Collection_helper]

  implicit val _writes: Writes[KeyedEntity[Long, Collection_helper]] =
    ((JsPath \ "id").write[Long] and (JsPath).write[Collection_helper])(
      unlift(KeyedEntity.unapply[Long, Collection_helper]),
    )

  implicit val _reads: Reads[KeyedEntity[Long, Collection_helper]] =
    ((JsPath \ "id").read[Long] and (JsPath).read[Collection_helper])(
      KeyedEntity.apply[Long, Collection_helper] _,
    )

  implicit val _format: Format[KeyedEntity[Long, Collection_helper]] = Format(
    _reads,
    _writes,
  )

}
