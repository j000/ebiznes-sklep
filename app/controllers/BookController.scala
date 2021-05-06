package controllers

import javax.inject._
import play.api.mvc.{ Action, AnyContent }
import play.api.mvc.{ ControllerComponents, InjectedController }
import play.api.libs.json._
import play.api.mvc.Request
import models.BookModel._
import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class BookController @Inject() (
  val repo: BookRepository,
)(
  implicit
  ec: ExecutionContext,
) extends InjectedController {
  type BookDBO = repo.DBO

  def index() = Action.async {
    repo
      .index()
      .map { books =>
        Ok(Json.toJson(books))
      }
  }

  def create() = Action(parse.json).async { request =>
    request
      .body
      .validate[Book]
      .fold(
        problems => {
          Future(BadRequest("Invalid json content"))
        },
        input => {
          repo
            .create(input)
            .map { book =>
              Ok(Json.toJson(book))
            }
        },
      )
  }

  def read(id: Long) = Action.async {
    repo
      .read(id)
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
          .update(id, bookData)
          .map {
            case None =>
              NotFound(Json.obj("error" -> "Not Found"))
            case book =>
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
