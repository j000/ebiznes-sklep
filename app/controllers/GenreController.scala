package controllers

import javax.inject._
import play.api.mvc.{ Action, AnyContent }
import play.api.mvc.{ ControllerComponents, InjectedController }
import play.api.libs.json._
import play.api.mvc.{ MessagesActionBuilder, MessagesRequest, Request, Result }
import models.GenreModel._
import scala.concurrent.{ ExecutionContext, Future }
import play.api.data.Forms._
import play.api.data.Form
import scala.util.{ Failure, Success }
import utils.DBImplicits

@Singleton
class GenreController @Inject() (
  repo: GenreRepository,
  messagesAction: MessagesActionBuilder,
  dbExecuter: DBImplicits,
)(
  implicit
  ec: ExecutionContext,
) extends InjectedController {
  import dbExecuter.executeOperation

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
        problems => Future(BadRequest("Invalid json content")),
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
        Future(BadRequest(Json.obj("error" -> "Invalid Json")))
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
          Ok(s"genre $id deleted")
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
        Ok(views.html.genre.list(genres))
      }
  }

  def saveForm(
  ) = messagesAction.async { implicit request: MessagesRequest[AnyContent] =>
    val formValidationResult = form.bindFromRequest()
    formValidationResult.fold(
      { formWithErrors: Form[Genre] =>
        Future(BadRequest(views.html.genre.add(formWithErrors)))
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
    Future(Ok(views.html.genre.add(form)))
  }

  def updateForm(
    id: Long,
  ) = messagesAction.async { implicit request: MessagesRequest[AnyContent] =>
    val formValidationResult = form.bindFromRequest()
    formValidationResult.fold(
      { formWithErrors: Form[Genre] =>
        Future(BadRequest(views.html.genre.edit(id, formWithErrors)))
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
          Ok(views.html.genre.edit(id, form.fill(data)))
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
