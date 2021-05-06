package controllers

import javax.inject._
import play.api.mvc.{ Action, AnyContent }
import play.api.mvc.{ ControllerComponents, InjectedController }
import play.api.libs.json._
import play.api.mvc.Request
import models.PaymentModel._
import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class PaymentController @Inject() (
  val repo: PaymentRepository,
)(
  implicit
  ec: ExecutionContext,
) extends InjectedController {
  type PaymentDBO = repo.DBO

  def index() = Action.async {
    repo
      .index()
      .map { payments =>
        Ok(Json.toJson(payments))
      }
  }

  def create() = Action(parse.json).async { request =>
    request
      .body
      .validate[Payment]
      .fold(
        problems => {
          Future(BadRequest("Invalid json content"))
        },
        input => {
          repo
            .create(input)
            .map { payment =>
              Ok(Json.toJson(payment))
            }
        },
      )
  }

  def read(id: Long) = Action.async {
    repo
      .read(id)
      .map {
        case Some(payment) =>
          Ok(Json.toJson(payment))
        case _ =>
          NotFound(Json.obj("error" -> "Not Found"))
      }
  }

  def update(id: Long) = Action(parse.json).async { implicit request =>
    val paymentResult = request.body.validate[Payment]
    paymentResult.fold(
      errors => {
        Future(BadRequest(Json.obj("error" -> "Invalid Json")))
      },
      paymentData => {
        repo
          .update(id, paymentData)
          .map {
            case None =>
              NotFound(Json.obj("error" -> "Not Found"))
            case payment =>
              Ok(Json.toJson(payment))
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
          Ok(s"payment $id deleted")
      }
  }

}
