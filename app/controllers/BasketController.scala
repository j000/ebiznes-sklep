package controllers

import javax.inject._
import play.api.mvc.{ Action, AnyContent }
import play.api.mvc.{ ControllerComponents, InjectedController }
import play.api.libs.json._
import play.api.mvc.Request
import models.BasketModel._
import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class BasketController @Inject() (
  val repo: BasketRepository,
)(
  implicit
  ec: ExecutionContext,
) extends InjectedController {
  type BasketDBO = repo.DBO

  def index() = Action.async {
    repo
      .index()
      .map { baskets =>
        Ok(Json.toJson(baskets))
      }
  }

  def create() = Action(parse.json).async { request =>
    request
      .body
      .validate[Basket]
      .fold(
        problems => {
          Future(BadRequest("Invalid json content"))
        },
        input => {
          repo
            .create(input)
            .map { basket =>
              Ok(Json.toJson(basket))
            }
        },
      )
  }

  def read(id: Long) = Action.async {
    repo
      .read(id)
      .map {
        case Some(basket) =>
          Ok(Json.toJson(basket))
        case _ =>
          NotFound(Json.obj("error" -> "Not Found"))
      }
  }

  def update(id: Long) = Action(parse.json).async { implicit request =>
    val basketResult = request.body.validate[Basket]
    basketResult.fold(
      errors => {
        Future(BadRequest(Json.obj("error" -> "Invalid Json")))
      },
      basketData => {
        repo
          .update(id, basketData)
          .map {
            case None =>
              NotFound(Json.obj("error" -> "Not Found"))
            case basket =>
              Ok(Json.toJson(basket))
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
          Ok(Json.obj("status" -> s"basket $id deleted"))
      }
  }

}
