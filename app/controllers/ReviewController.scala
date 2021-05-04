package controllers

import javax.inject._
import play.api.mvc.{Action, AnyContent}
import play.api.mvc.{InjectedController, ControllerComponents}
import play.api.libs.json._
import play.api.mvc.Request
import models.ReviewModel._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ReviewController @Inject()(
  val repo: ReviewRepository,
)(
  implicit ec: ExecutionContext
) extends InjectedController
{
  type ReviewDBO = repo.DBO

  def index() = Action.async {
    repo.index().map { reviews =>
      Ok(Json.toJson(reviews))
    }
  }
  def create() = Action(parse.json).async { request =>
    request.body.validate[Review].fold(
      problems => {
        Future(BadRequest("Invalid json content"))
      },
      input => {
        repo.create(input).map {
          review => Ok(Json.toJson(review))
        }
      }
    )
  }
  def read(id: Long) = Action.async {
    repo.read(id).map {
      case Some(review) => Ok(Json.toJson(review))
      case _ => NotFound(Json.obj("error" -> "Not Found"))
    }
  }
  def update(id: Long) = Action(parse.json).async { implicit request =>
    val reviewResult = request.body.validate[Review]
    reviewResult.fold(
      errors => {
        Future(BadRequest(Json.obj("error" -> "Invalid Json")))
      },
      reviewData => {
        repo.update(id, reviewData).map {
          case None => NotFound(Json.obj("error" -> "Not Found"))
          case review => Ok(Json.toJson(review))
        }
      }
    )
  }
  def delete(id: Long) = Action.async {
    repo.delete(id).map {
      case 0 => NotFound(Json.obj("error" -> "Not Found"))
      case _ => Ok(s"review $id deleted")
    }
  }
}
