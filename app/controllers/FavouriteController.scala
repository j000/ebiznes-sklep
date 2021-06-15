package controllers

import javax.inject.{ Inject, Singleton }
import play.api.mvc._
import play.api.libs.json._
import models.FavouriteModel._
import scala.concurrent.{ ExecutionContext, Future }
import play.api.data.Forms._
import play.api.data.Form
import utils.DBImplicits

@Singleton
class FavouriteController @Inject() (
  repo: FavouriteRepository,
  messagesAction: MessagesActionBuilder,
)(
  implicit
  ec: ExecutionContext,
  dbExecutor: DBImplicits,
) extends InjectedController {
  import dbExecutor.executeOperation
  import views.html.favourite._

  def index() = Action.async {
    repo
      .findAll()
      .map { favourites =>
        Ok(Json.toJson(favourites))
      }
  }

  def create() = Action(parse.json).async { implicit request =>
    request
      .body
      .validate[Favourite]
      .fold(
        problems => Future.successful(BadRequest(Json.obj("error" -> "Invalid Json"))),
        input => {
          repo
            .save(input.copy(id = None))
            .map { favourite =>
              Ok(Json.toJson(favourite))
            }
        },
      )
  }

  def read(id: Long) = Action.async {
    repo
      .findOne(id)
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
          .update(favouriteData.withId(id))
          .map { favourite =>
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

  val form = Form(
    mapping(
      "id" -> ignored(None: Option[Long]),
      "user_id" -> longNumber,
      "book_id" -> optional(longNumber),
      "author_id" -> optional(longNumber),
      "genre_id" -> optional(longNumber),
    )(Favourite.apply)(Favourite.unapply),
  )

  def listForm(
  ) = messagesAction.async { implicit request: MessagesRequest[AnyContent] =>
    repo
      .findAll()
      .map { favourites =>
        Ok(listView(favourites))
      }
  }

  def saveForm(
  ) = messagesAction.async { implicit request: MessagesRequest[AnyContent] =>
    val formValidationResult = form.bindFromRequest()
    formValidationResult.fold(
      { formWithErrors: Form[Favourite] =>
        Future.successful(BadRequest(editView(formWithErrors)))
      },
      { data: Favourite =>
        repo
          .save(data.copy(id = None))
          .map { _ =>
            Redirect(routes.FavouriteController.listForm())
              .flashing("info" -> "Favourite added!")
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
      { formWithErrors: Form[Favourite] =>
        Future.successful(BadRequest(editView(formWithErrors, id)))
      },
      { data: Favourite =>
        repo
          .update(data.withId(id))
          .map { _ =>
            Redirect(routes.FavouriteController.listForm())
              .flashing("info" -> "Favourite modified!")
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
        Redirect(routes.FavouriteController.listForm())
          .flashing("info" -> "Favourite deleted!")
      }
  }

}
