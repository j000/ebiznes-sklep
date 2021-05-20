package controllers

import javax.inject._
import play.api.mvc.{ Action, AnyContent }
import play.api.mvc.{ ControllerComponents, InjectedController }
import play.api.libs.json._
import play.api.mvc.Request
import models.FavouriteModel._
import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class FavouriteController @Inject() (
  val repo: FavouriteRepository,
)(
  implicit
  ec: ExecutionContext,
) extends InjectedController {
  type FavouriteDBO = repo.DBO

  def index() = Action.async {
    repo
      .index()
      .map { favourites =>
        Ok(Json.toJson(favourites))
      }
  }

  def create() = Action(parse.json).async { request =>
    request
      .body
      .validate[Favourite]
      .fold(
        problems => {
          Future.successful(BadRequest(Json.obj("error" -> "Invalid Json")))
        },
        input => {
          repo
            .create(input)
            .map { favourite =>
              Ok(Json.toJson(favourite))
            }
        },
      )
  }

  def read(id: Long) = Action.async {
    repo
      .read(id)
      .map {
        case Some(favourite) =>
          Ok(Json.toJson(favourite))
        case _ =>
          NotFound(Json.obj("error" -> "Not Found"))
      }
  }

  def update(id: Long) = Action(parse.json).async { implicit request =>
    val favouriteResult = request.body.validate[Favourite]
    favouriteResult.fold(
      errors => {
        Future.successful(BadRequest(Json.obj("error" -> "Invalid Json")))
      },
      favouriteData => {
        repo
          .update(id, favouriteData)
          .map {
            case None =>
              NotFound(Json.obj("error" -> "Not Found"))
            case favourite =>
              Ok(Json.toJson(favourite))
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
          Ok(Json.obj("status" -> s"favourite $id deleted"))
      }
  }

}
