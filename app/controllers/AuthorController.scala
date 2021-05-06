package controllers

import javax.inject._
import play.api.mvc.{ Action, AnyContent }
import play.api.mvc.{ ControllerComponents, InjectedController }
import play.api.libs.json._
import play.api.mvc.Request
import models.AuthorModel._
import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class AuthorController @Inject() (
  val repo: AuthorRepository,
)(
  implicit
  ec: ExecutionContext,
) extends InjectedController {
  type AuthorDBO = repo.DBO

  def index() = Action.async {
    repo
      .index()
      .map { authors =>
        Ok(Json.toJson(authors))
      }
  }

  def create() = Action(parse.json).async { request =>
    request
      .body
      .validate[Author]
      .fold(
        problems => {
          Future(BadRequest("Invalid json content"))
        },
        input => {
          repo
            .create(input)
            .map { author =>
              Ok(Json.toJson(author))
            }
        },
      )
  }

  def read(id: Long) = Action.async {
    repo
      .read(id)
      .map {
        case Some(author) =>
          Ok(Json.toJson(author))
        case _ =>
          NotFound(Json.obj("error" -> "Not Found"))
      }
  }

  def update(id: Long) = Action(parse.json).async { implicit request =>
    val authorResult = request.body.validate[Author]
    authorResult.fold(
      errors => {
        Future(BadRequest(Json.obj("error" -> "Invalid Json")))
      },
      authorData => {
        repo
          .update(id, authorData)
          .map {
            case None =>
              NotFound(Json.obj("error" -> "Not Found"))
            case author =>
              Ok(Json.toJson(author))
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
          Ok(s"author $id deleted")
      }
  }

}
