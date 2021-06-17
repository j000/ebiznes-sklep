package controllers

import javax.inject.{ Inject, Singleton }
import play.api.mvc._
import play.api.libs.json._
import models.OrderModel._
import scala.concurrent.{ ExecutionContext, Future }
import play.api.data.Forms._
import play.api.data.Form
import utils.DBImplicits

@Singleton
class OrderController @Inject() (
  repo: OrderRepository,
  messagesAction: MessagesActionBuilder,
)(
  implicit
  ec: ExecutionContext,
  dbExecutor: DBImplicits,
) extends InjectedController {
  import dbExecutor.executeOperation
  import views.html.order._

  def index() = Action.async {
    repo
      .findAll()
      .map { orders =>
        Ok(Json.toJson(orders))
      }
  }

  def create() = Action(parse.json).async { implicit request =>
    request
      .body
      .validate[Order]
      .fold(
        problems =>
          Future.successful(BadRequest(Json.obj("error" -> "Invalid Json"))),
        input => {
          repo
            .save(input.copy(id = None))
            .map { order =>
              Ok(Json.toJson(order))
            }
        },
      )
  }

  def read(id: Long) = Action.async {
    repo
      .findOne(id)
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
        Future.successful(BadRequest(Json.obj("error" -> "Invalid Json")))
      },
      orderData => {
        repo
          .update(orderData.withId(id))
          .map { order =>
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
          Ok(Json.obj("status" -> s"order $id deleted"))
      }
  }

  val form = Form(
    mapping("id" -> ignored(None: Option[Long]), "user_id" -> longNumber)(
      Order.apply,
    )(Order.unapply),
  )

  def listForm(
  ) = messagesAction.async { implicit request: MessagesRequest[AnyContent] =>
    repo
      .findAll()
      .map { orders =>
        Ok(listView(orders))
      }
  }

  def saveForm(
  ) = messagesAction.async { implicit request: MessagesRequest[AnyContent] =>
    val formValidationResult = form.bindFromRequest()
    formValidationResult.fold(
      { formWithErrors: Form[Order] =>
        Future.successful(BadRequest(editView(formWithErrors)))
      },
      { data: Order =>
        repo
          .save(data.copy(id = None))
          .map { _ =>
            Redirect(routes.OrderController.listForm())
              .flashing("info" -> "Order added!")
          }
      },
    )
  }

  def createForm(
  ) = messagesAction.async { implicit request: MessagesRequest[AnyContent] =>
    Future.successful(Ok(editView(form)))
  }

  def updateForm(
    id: Long,
  ) = messagesAction.async { implicit request: MessagesRequest[AnyContent] =>
    val formValidationResult = form.bindFromRequest()
    formValidationResult.fold(
      { formWithErrors: Form[Order] =>
        Future.successful(BadRequest(editView(formWithErrors, id)))
      },
      { data: Order =>
        repo
          .update(data.withId(id))
          .map { _ =>
            Redirect(routes.OrderController.listForm())
              .flashing("info" -> "Order modified!")
          }
      },
    )
  }

  def editForm(
    id: Long,
  ) = messagesAction.async { implicit request: MessagesRequest[AnyContent] =>
    repo
      .findOne(id)
      .map {
        case Some(data) =>
          Ok(editView(form.fill(data), id))
        case None =>
          NotFound
      }
  }

  def deleteForm(
    id: Long,
  ) = Action.async {
    repo
      .delete(id)
      .map { _ =>
        Redirect(routes.OrderController.listForm())
          .flashing("info" -> "Order deleted!")
      }
  }

}
