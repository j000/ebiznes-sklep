package controllers

import javax.inject._
import play.api.mvc.{Action, AnyContent}
import play.api.mvc.{AbstractController, ControllerComponents}
import play.api.libs.json._
import models.{Author,AuthorRepository}
import scala.concurrent.{ExecutionContext, Future}

case class AuthorData(name: String)
object AuthorData {
  implicit val authorDataFormat = Json.format[AuthorData]
}

@Singleton
class AuthorController @Inject()(
  val repo: AuthorRepository,
  val cc: ControllerComponents
)(
  implicit ec: ExecutionContext
) extends AbstractController(cc)
{
  def index = Action.async {
    repo.list().map { authors =>
      Ok(Json.toJson(authors))
    }
  }
  def show(id: Long) = Action.async {
    repo.get(id).map { author =>
      if (author == None)
        NotFound(Json.obj("error" -> "Not Found"))
      else
        Ok(Json.toJson(author))
    }
  }
  def create() = Action(parse.json).async { implicit request =>
    val authorResult = request.body.validate[AuthorData]
    authorResult.fold(
      errors => {
        Future(BadRequest(Json.obj("error" -> JsError.toJson(errors))))
      },
      authorData => {
        repo.create(authorData.name).map { author =>
          Ok(s"author created ${author.id}")
        }
      }
    )
  }
  def update(id: Long) = Action {
    Ok(s"author $id updated")
  }
  def delete(id: Long) = Action {
    Ok(s"author $id deleted")
  }
}
