package controllers

import javax.inject.{ Inject, Singleton }
import play.api.mvc._
import play.api.libs.json._
import models.GenreModel._
import scala.concurrent.{ ExecutionContext, Future }
import play.api.data.Forms._
import play.api.data.Form
import utils.DBImplicits

@Singleton
class GenreController @Inject() (
  repo: GenreRepository,
  messagesAction: MessagesActionBuilder,
)(
  implicit
  ec: ExecutionContext,
  dbExecutor: DBImplicits,
) extends InjectedController {
  import dbExecutor.executeOperation
  import views.html.genre._

  def index() = Action.async {
    repo
      .findAll()
      .map { genres =>
        Ok(Json.toJson(genres))
      }
  }

  def create() = Action(parse.json).async { implicit request =>
    request
      .body
      .validate[Genre]
      .fold(
        problems => Future.successful(BadRequest(Json.obj("error" -> "Invalid Json"))),
        input => {
          repo
            .save(input.copy(id = None))
            .map { genre =>
              Ok(Json.toJson(genre))
            }
        },
      )
  }

  def read(id: Long) = Action.async {
    repo
      .findOne(id)
      .map {
        case Some(genre) =>
          Ok(Json.toJson(genre))
        case _ =>
          NotFound(Json.obj("error" -> "Not Found"))
      }
  }

  def update(id: Long) = Action(parse.json).async { implicit request =>
    val genreResult = request.body.validate[Genre]
    genreResult.fold(
      errors => {
        Future.successful(BadRequest(Json.obj("error" -> "Invalid Json")))
      },
      genreData => {
        repo
          .update(genreData.withId(id))
          .map { genre =>
            Ok(Json.toJson(genre))
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
          Ok(Json.obj("status" -> s"genre $id deleted"))
      }
  }

  val form = Form(
    mapping("id" -> ignored(None: Option[Long]), "name" -> nonEmptyText)(
      Genre.apply,
    )(Genre.unapply),
  )

  def listForm(
  ) = messagesAction.async { implicit request: MessagesRequest[AnyContent] =>
    repo
      .findAll()
      .map { genres =>
        Ok(listView(genres))
      }
  }

  def saveForm(
  ) = messagesAction.async { implicit request: MessagesRequest[AnyContent] =>
    val formValidationResult = form.bindFromRequest()
    formValidationResult.fold(
      { formWithErrors: Form[Genre] =>
        Future.successful(BadRequest(editView(formWithErrors)))
      },
      { data: Genre =>
        repo
          .save(data.copy(id = None))
          .map { _ =>
            Redirect(routes.GenreController.listForm())
              .flashing("info" -> "Genre added!")
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
      { formWithErrors: Form[Genre] =>
        Future.successful(BadRequest(editView(formWithErrors, id)))
      },
      { data: Genre =>
        repo
          .update(data.withId(id))
          .map { _ =>
            Redirect(routes.GenreController.listForm())
              .flashing("info" -> "Genre modified!")
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
        Redirect(routes.GenreController.listForm())
          .flashing("info" -> "Genre deleted!")
      }
  }

}
