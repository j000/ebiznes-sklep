package controllers

import javax.inject.{ Inject, Singleton }
import play.api.mvc._
import play.api.libs.json._
import models.BasketModel._
import scala.concurrent.{ ExecutionContext, Future }
import play.api.data.Forms._
import play.api.data.Form
import utils.DBImplicits

@Singleton
class BasketController @Inject() (
  repo: BasketRepository,
  messagesAction: MessagesActionBuilder,
  dbExecutor: DBImplicits,
)(
  implicit
  ec: ExecutionContext,
) extends InjectedController {
  import dbExecutor.executeOperation
  import views.html.basket._

  def index() = Action.async {
    repo
      .findAll()
      .map { baskets =>
        Ok(Json.toJson(baskets))
      }
  }

  def create() = Action(parse.json).async { implicit request =>
    request
      .body
      .validate[Basket]
      .fold(
        problems => Future.successful(BadRequest(Json.obj("error" -> "Invalid Json"))),
        input => {
          repo
            .save(input.copy(id = None))
            .map { basket =>
              Ok(Json.toJson(basket))
            }
        },
      )
  }

  def read(id: Long) = Action.async {
    repo
      .findOne(id)
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
        Future.successful(BadRequest(Json.obj("error" -> "Invalid Json")))
      },
      basketData => {
        repo
          .update(basketData.withId(id))
          .map { basket =>
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

  val form = Form(
    mapping(
      "id" -> ignored(None: Option[Long]),
      "user_id" -> longNumber,
      "book_id" -> longNumber,
      "count" -> longNumber,
    )(Basket.apply)(Basket.unapply),
  )

  def listForm(
  ) = messagesAction.async { implicit request: MessagesRequest[AnyContent] =>
    repo
      .findAll()
      .map { baskets =>
        Ok(listView(baskets))
      }
  }

  def saveForm(
  ) = messagesAction.async { implicit request: MessagesRequest[AnyContent] =>
    val formValidationResult = form.bindFromRequest()
    formValidationResult.fold(
      { formWithErrors: Form[Basket] =>
        Future.successful(BadRequest(editView(formWithErrors)))
      },
      { data: Basket =>
        repo
          .save(data.copy(id = None))
          .map { _ =>
            Redirect(routes.BasketController.listForm())
              .flashing("info" -> "Basket added!")
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
      { formWithErrors: Form[Basket] =>
        Future.successful(BadRequest(editView(formWithErrors, id)))
      },
      { data: Basket =>
        repo
          .update(data.withId(id))
          .map { _ =>
            Redirect(routes.BasketController.listForm())
              .flashing("info" -> "Basket modified!")
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
        Redirect(routes.BasketController.listForm())
          .flashing("info" -> "Basket deleted!")
      }
  }

}
