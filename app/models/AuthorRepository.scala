package models

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ Future, ExecutionContext }
import play.api.libs.json._

case class Author(id: Long, name: String)

object Author {
  implicit val authorFormat = Json.format[Author]
}

@Singleton
class AuthorRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  // We want the JdbcProfile for this provider
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  // These imports are important, the first one brings db into scope, which will let you do the actual db operations.
  // The second one brings the Slick DSL into scope, which lets you define the table and other queries.
  import dbConfig._
  import profile.api._

  /**
    * Here we define the table. It will have a name of authors
    */
  private class AuthorsTable(tag: Tag) extends Table[Author](tag, "authors") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")

    /**
      * This is the tables default "projection".
      *
      * It defines how the columns are converted to and from the Author object.
      *
      * In this case, we are simply passing the id, name and page parameters to the Author case classes
      * apply and unapply methods.
      */
    def * = (id, name) <> ((Author.apply _).tupled, Author.unapply)
  }

  /**
    * The starting point for all queries on the authors table.
    */
  private val authors = TableQuery[AuthorsTable]

  def create(name: String): Future[Author] = db.run {
    (authors.map(a => (a.name))
      returning authors.map(_.id)
      into ((name, id) => Author(id, name))
    ) += (name)
  }

  def list(): Future[Seq[Author]] = db.run {
    authors.result
  }

  def get(id: Long): Future[Option[Author]] = db.run {
    authors.filter(_.id === id).result.headOption
  }
}

