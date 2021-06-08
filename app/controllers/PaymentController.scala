package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import models.PaymentModel._
import scala.concurrent.{ ExecutionContext, Future }
import play.api.data.Forms._
import play.api.data.Form
import utils.DBImplicits

@Singleton
class PaymentController @Inject() (
  repo: PaymentRepository,
  messagesAction: MessagesActionBuilder,
  dbExecuter: DBImplicits,
)(
  implicit
  ec: ExecutionContext,
) extends InjectedController {
  import dbExecuter.executeOperation
  import views.html.payment._

  def index() = Action.async {
    repo
      .findAll()
      .map { payments =>
        Ok(Json.toJson(payments))
      }
  }

  def create() = Action(parse.json).async { implicit request =>
    request
      .body
      .validate[Payment]
      .fold(
        problems => Future.successful(BadRequest(Json.obj("error" -> "Invalid Json"))),
        input => {
          repo
            .save(input.copy(id = None))
            .map { payment =>
              Ok(Json.toJson(payment))
            }
        },
      )
  }

  def read(id: Long) = Action.async {
    repo
      .findOne(id)
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
        Future.successful(BadRequest(Json.obj("error" -> "Invalid Json")))
      },
      paymentData => {
        repo
          .update(paymentData.withId(id))
          .map { payment =>
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
          Ok(Json.obj("status" -> s"payment $id deleted"))
      }
  }

  val form = Form(
    mapping(
      "id" -> ignored(None: Option[Long]),
      "order_id" -> longNumber,
      "amount" -> longNumber,
      "comment" -> optional(text),
    )(
      Payment.apply,
    )(Payment.unapply),
  )

  def listForm(
  ) = messagesAction.async { implicit request: MessagesRequest[AnyContent] =>
    repo
      .findAll()
      .map { payments =>
        Ok(listView(payments))
      }
  }

  def saveForm(
  ) = messagesAction.async { implicit request: MessagesRequest[AnyContent] =>
    val formValidationResult = form.bindFromRequest()
    formValidationResult.fold(
      { formWithErrors: Form[Payment] =>
        Future.successful(BadRequest(editView(formWithErrors)))
      },
      { data: Payment =>
        repo
          .save(data.copy(id = None))
          .map { _ =>
            Redirect(routes.PaymentController.listForm())
              .flashing("info" -> "Payment added!")
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
      { formWithErrors: Form[Payment] =>
        Future.successful(BadRequest(editView(formWithErrors, id)))
      },
      { data: Payment =>
        repo
          .update(data.withId(id))
          .map { _ =>
            Redirect(routes.PaymentController.listForm())
              .flashing("info" -> "Payment modified!")
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
        Redirect(routes.PaymentController.listForm())
          .flashing("info" -> "Payment deleted!")
      }
  }

}
