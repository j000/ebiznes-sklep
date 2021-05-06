package controllers

import javax.inject._
import play.api.mvc.{ Action, AnyContent }
import play.api.mvc.{ ControllerComponents, InjectedController }
import play.api.libs.json._
import play.api.mvc.Request
import models.OrderModel._
import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class OrderController @Inject() (
  val repo: OrderRepository,
)(
  implicit
  ec: ExecutionContext,
) extends InjectedController {
  type OrderDBO = repo.DBO

  def index() = Action.async {
    repo
      .index()
      .map { orders =>
        Ok(Json.toJson(orders))
      }
  }

  def create() = Action(parse.json).async { request =>
    request
      .body
      .validate[Order]
      .fold(
        problems => {
          Future(BadRequest("Invalid json content"))
        },
        input => {
          repo
            .create(input)
            .map { order =>
              Ok(Json.toJson(order))
            }
        },
      )
  }

  def read(id: Long) = Action.async {
    repo
      .read(id)
      .map {
        case Some(order) =>
          Ok(Json.toJson(order))
        case _ =>
          NotFound(Json.obj("error" -> "Not Found"))
      }
  }

  def update(id: Long) = Action(parse.json).async { implicit request =>
    val orderResult = request.body.validate[Order]
    orderResult.fold(
      errors => {
        Future(BadRequest(Json.obj("error" -> "Invalid Json")))
      },
      orderData => {
        repo
          .update(id, orderData)
          .map {
            case None =>
              NotFound(Json.obj("error" -> "Not Found"))
            case order =>
              Ok(Json.toJson(order))
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
          Ok(s"order $id deleted")
      }
  }

}
