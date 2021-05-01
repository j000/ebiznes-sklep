package controllers

import javax.inject._
import play.api.mvc.{Action, AnyContent}
import play.api.mvc.{AbstractController, ControllerComponents}
import play.api.libs.json._
import models.{Author,AuthorData,AuthorRepository}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AuthorController @Inject()(
  val repo: AuthorRepository,
  val cc: ControllerComponents
)(
  implicit ec: ExecutionContext
) extends AbstractController(cc)
{
  def index = Action.async {
    repo.index().map { authors =>
      Ok(Json.toJson(authors))
    }
  }
  def create() = Action(parse.json).async { implicit request =>
    val authorResult = request.body.validate[AuthorData]
    authorResult.fold(
      errors => {
        Future(BadRequest(Json.obj("error" -> JsError.toJson(errors))))
      },
      authorData => {
        repo.create(authorData).map { author =>
          Ok(s"author ${author.id} created")
        }
      }
    )
  }
  def read(id: Long) = Action.async {
    repo.read(id).map { author =>
      if (author == None)
        NotFound(Json.obj("error" -> "Not Found"))
      else
        Ok(Json.toJson(author))
    }
  }
  def update(id: Long) = Action(parse.json).async { implicit request =>
    val authorResult = request.body.validate[AuthorData]
    authorResult.fold(
      errors => {
        Future(BadRequest(Json.obj("error" -> JsError.toJson(errors))))
      },
      authorData => {
        repo.update(id, authorData).map {
          case 0 => NotFound(Json.obj("error" -> "Not Found"))
          case _ => Ok(s"author ${id} updated")
        }
      }
    )
  }
  def delete(id: Long) = Action.async {
    repo.delete(id).map {
      case 0 => NotFound(Json.obj("error" -> "Not Found"))
      case _ => Ok(s"author $id deleted")
    }
  }
}
