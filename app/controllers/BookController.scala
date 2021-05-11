package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import models.BookModel._
import scala.concurrent.{ ExecutionContext, Future }
import play.api.data.Forms._
import play.api.data.Form
import utils.DBImplicits

@Singleton
class BookController @Inject() (
  repo: BookRepository,
  messagesAction: MessagesActionBuilder,
  dbExecuter: DBImplicits,
)(
  implicit
  ec: ExecutionContext,
) extends InjectedController {
  import dbExecuter.executeOperation
  import views.html.book._

  def index() = Action.async {
    repo
      .findAll()
      .map { books =>
        Ok(Json.toJson(books))
      }
  }

  def create() = Action(parse.json).async { implicit request =>
    request
      .body
      .validate[Book]
      .fold(
        problems => Future(BadRequest("Invalid json content")),
        input => {
          repo
            .save(input.copy(id = None))
            .map { book =>
              Ok(Json.toJson(book))
            }
        },
      )
  }

  def read(id: Long) = Action.async {
    repo
      .findOne(id)
      .map {
        case Some(book) =>
          Ok(Json.toJson(book))
        case _ =>
          NotFound(Json.obj("error" -> "Not Found"))
      }
  }

  def update(id: Long) = Action(parse.json).async { implicit request =>
    val bookResult = request.body.validate[Book]
    bookResult.fold(
      errors => {
        Future(BadRequest(Json.obj("error" -> "Invalid Json")))
      },
      bookData => {
        repo
          .update(bookData.withId(id))
          .map { book =>
            Ok(Json.toJson(book))
          }
      },
    )
  }

  def delete(id: Long) = Action.async {
    repo
      .delete(id)
      .map {
        case 0 =>
          NotFound(Json.obj("error" -> "Not Found"))
        case _ =>
          Ok(s"book $id deleted")
      }
  }

}
