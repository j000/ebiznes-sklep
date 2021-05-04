package controllers

import javax.inject._
import play.api.mvc.{Action, AnyContent}
import play.api.mvc.{InjectedController, ControllerComponents}
import play.api.libs.json._
import play.api.mvc.Request
import models.CollectionModel._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CollectionController @Inject()(
  val repo: CollectionRepository,
)(
  implicit ec: ExecutionContext
) extends InjectedController
{
  type CollectionDBO = repo.DBO

  def index() = Action.async {
    repo.index().map { collections =>
      Ok(Json.toJson(collections))
    }
  }
  def create() = Action(parse.json).async { request =>
    request.body.validate[Collection].fold(
      problems => {
        Future(BadRequest("Invalid json content"))
      },
      input => {
        repo.create(input).map {
          collection => Ok(Json.toJson(collection))
        }
      }
    )
  }
  def read(id: Long) = Action.async {
    repo.read(id).map {
      case Some(collection) => Ok(Json.toJson(collection))
      case _ => NotFound(Json.obj("error" -> "Not Found"))
    }
  }
  def update(id: Long) = Action(parse.json).async { implicit request =>
    val collectionResult = request.body.validate[Collection]
    collectionResult.fold(
      errors => {
        Future(BadRequest(Json.obj("error" -> "Invalid Json")))
      },
      collectionData => {
        repo.update(id, collectionData).map {
          case None => NotFound(Json.obj("error" -> "Not Found"))
          case collection => Ok(Json.toJson(collection))
        }
      }
    )
  }
  def delete(id: Long) = Action.async {
    repo.delete(id).map {
      case 0 => NotFound(Json.obj("error" -> "Not Found"))
      case _ => Ok(s"collection $id deleted")
    }
  }
}
