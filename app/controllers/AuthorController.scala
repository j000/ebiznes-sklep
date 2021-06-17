package controllers

import javax.inject.{ Inject, Singleton }
import play.api.mvc._
import play.api.libs.json._
import models.AuthorModel._
import scala.concurrent.{ ExecutionContext, Future }
import play.api.data.Forms._
import play.api.data.Form
import utils.DBImplicits

@Singleton
class AuthorController @Inject() (
  repo: AuthorRepository,
  messagesAction: MessagesActionBuilder,
)(
  implicit
  ec: ExecutionContext,
  dbExecutor: DBImplicits,
) extends InjectedController {
  import dbExecutor.executeOperation
  import views.html.author._

  def index() = Action.async {
    repo
      .findAll()
      .map { authors =>
        Ok(Json.toJson(authors))
      }
  }

  def create() = Action(parse.json).async { implicit request =>
    request
      .body
      .validate[Author]
      .fold(
        problems =>
          Future.successful(BadRequest(Json.obj("error" -> "Invalid Json"))),
        input => {
          repo
            .save(input.copy(id = None))
            .map { author =>
              Ok(Json.toJson(author))
            }
        },
      )
  }

  def read(id: Long) = Action.async {
    repo
      .findOne(id)
      .map {
        case Some(author) =>
          Ok(Json.toJson(author))
        case _ =>
          NotFound(Json.obj("error" -> "Not Found"))
      }
  }

  def update(id: Long) = Action(parse.json).async { implicit request =>
    val authorResult = request.body.validate[Author]
    authorResult.fold(
      errors => {
        Future.successful(BadRequest(Json.obj("error" -> "Invalid Json")))
      },
      authorData => {
        repo
          .update(authorData.withId(id))
          .map { author =>
            Ok(Json.toJson(author))
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
          Ok(Json.obj("status" -> s"author $id deleted"))
      }
  }

  val form = Form(
    mapping("id" -> ignored(None: Option[Long]), "name" -> nonEmptyText)(
      Author.apply,
    )(Author.unapply),
  )

  def listForm(
  ) = messagesAction.async { implicit request: MessagesRequest[AnyContent] =>
    repo
      .findAll()
      .map { authors =>
        Ok(listView(authors))
      }
  }

  def saveForm(
  ) = messagesAction.async { implicit request: MessagesRequest[AnyContent] =>
    val formValidationResult = form.bindFromRequest()
    formValidationResult.fold(
      { formWithErrors: Form[Author] =>
        Future.successful(BadRequest(editView(formWithErrors)))
      },
      { data: Author =>
        repo
          .save(data.copy(id = None))
          .map { _ =>
            Redirect(routes.AuthorController.listForm())
              .flashing("info" -> "Author added!")
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
      { formWithErrors: Form[Author] =>
        Future.successful(BadRequest(editView(formWithErrors, id)))
      },
      { data: Author =>
        repo
          .update(data.withId(id))
          .map { _ =>
            Redirect(routes.AuthorController.listForm())
              .flashing("info" -> "Author modified!")
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
        Redirect(routes.AuthorController.listForm())
          .flashing("info" -> "Author deleted!")
      }
  }

}
