package controllers

import javax.inject._
import play.api.mvc.{ Action, AnyContent }
import play.api.mvc.{ ControllerComponents, InjectedController }
import play.api.libs.json._
import play.api.mvc.Request
import models.GenreModel._
import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class GenreController @Inject() (
  val repo: GenreRepository,
)(
  implicit
  ec: ExecutionContext,
) extends InjectedController {
  type GenreDBO = repo.DBO

  def index() = Action.async {
    repo
      .index()
      .map { genres =>
        Ok(Json.toJson(genres))
      }
  }

  def create() = Action(parse.json).async { request =>
    request
      .body
      .validate[Genre]
      .fold(
        problems => {
          Future(BadRequest("Invalid json content"))
        },
        input => {
          repo
            .create(input)
            .map { genre =>
              Ok(Json.toJson(genre))
            }
        },
      )
  }

  def read(id: Long) = Action.async {
    repo
      .read(id)
      .map {
        case Some(genre) =>
          Ok(Json.toJson(genre))
        case _ =>
          NotFound(Json.obj("error" -> "Not Found"))
      }
  }

  def update(id: Long) = Action(parse.json).async { implicit request =>
    val genreResult = request.body.validate[Genre]
    genreResult.fold(
      errors => {
        Future(BadRequest(Json.obj("error" -> "Invalid Json")))
      },
      genreData => {
        repo
          .update(id, genreData)
          .map {
            case None =>
              NotFound(Json.obj("error" -> "Not Found"))
            case genre =>
              Ok(Json.toJson(genre))
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
          Ok(s"genre $id deleted")
      }
  }

}
