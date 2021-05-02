package models

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ Future, ExecutionContext }
import play.api.libs.json._

case class AuthorData(name: String)
object AuthorData {
  implicit val authorDataFormat = Json.format[AuthorData]
}

case class Author(name: String, id: Long = 0L)

object Author {
  implicit val authorFormat = Json.format[Author]
}

@Singleton
class AuthorRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private class AuthorsTable(tag: Tag) extends Table[Author](tag, "Authors") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")

    def * = (name, id) <> ((Author.apply _).tupled, Author.unapply)
  }

  private val authors = TableQuery[AuthorsTable]

  def index(): Future[Seq[Author]] = db.run {
    authors.result
  }

  def create(author: AuthorData): Future[Author] = db.run {
    (authors.map(_.name)
      returning authors.map(_.id)
      into ((name, id) => Author(name, id))
    ) += (author.name)
  }

  def read(id: Long): Future[Option[Author]] = db.run {
    authors.filter(_.id === id).result.headOption
  }

  def update(id: Long, author: AuthorData): Future[Int] = db.run {
    authors.filter(_.id === id).map(a => (a.name).mapTo[AuthorData]).update(author)
  }

  def delete(id: Long): Future[Int] = db.run {
    authors.filter(_.id === id).delete
  }
}

