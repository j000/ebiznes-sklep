package controllers

import javax.inject._
import play.api.mvc.{Action, AnyContent}
import play.api.mvc.{AbstractController, ControllerComponents}
import play.api.libs.json._
import models.{Genre,GenreData,GenreRepository}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GenreController @Inject()(
  val repo: GenreRepository,
  val cc: ControllerComponents
)(
  implicit ec: ExecutionContext
) extends AbstractController(cc)
{
  def index = Action.async {
    repo.index().map { genres =>
      Ok(Json.toJson(genres))
    }
  }
  def create() = Action(parse.json).async { implicit request =>
    request.body.validate[GenreData].fold(
      errors => {
        Future(BadRequest(Json.obj("error" -> JsError.toJson(errors))))
      },
      genreData => {
        repo.create(genreData).map { genre =>
          Ok(s"genre ${genre.id} created")
        }
      }
    )
  }
  def read(id: Long) = Action.async {
    repo.read(id).map { genre =>
      if (genre == None)
        NotFound(Json.obj("error" -> "Not Found"))
      else
        Ok(Json.toJson(genre))
    }
  }
  def update(id: Long) = Action(parse.json).async { implicit request =>
    val genreResult = request.body.validate[GenreData]
    genreResult.fold(
      errors => {
        Future(BadRequest(Json.obj("error" -> "Invalid Json")))
      },
      genreData => {
        repo.update(id, genreData).map {
          case 0 => NotFound(Json.obj("error" -> "Not Found"))
          case _ => Ok(s"genre ${id} updated")
        }
      }
    )
  }
  def delete(id: Long) = Action.async {
    repo.delete(id).map {
      case 0 => NotFound(Json.obj("error" -> "Not Found"))
      case _ => Ok(s"genre $id deleted")
    }
  }
}
