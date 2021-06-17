package controllers

import javax.inject.{ Inject, Singleton }
import play.api.mvc._
import play.api.libs.json._
import models.UserModel._
import scala.concurrent.{ ExecutionContext, Future }
import play.api.data.Forms._
import play.api.data.Form
import utils.DBImplicits

@Singleton
class UserController @Inject() (
  repo: UserRepository,
  messagesAction: MessagesActionBuilder,
)(
  implicit
  ec: ExecutionContext,
  dbExecutor: DBImplicits,
) extends InjectedController {
  import dbExecutor.executeOperation
  import views.html.user._

  def index() = Action.async {
    repo
      .findAll()
      .map { users =>
        Ok(Json.toJson(users))
      }
  }

  def create() = Action(parse.json).async { implicit request =>
    request
      .body
      .validate[User]
      .fold(
        problems =>
          Future.successful(BadRequest(Json.obj("error" -> "Invalid Json"))),
        input => {
          repo
            .save(input.copy(id = None))
            .map { user =>
              Ok(Json.toJson(user))
            }
        },
      )
  }

  def read(id: Long) = Action.async {
    repo
      .findOne(id)
      .map {
        case Some(user) =>
          Ok(Json.toJson(user))
        case _ =>
          NotFound(Json.obj("error" -> "Not Found"))
      }
  }

  def update(id: Long) = Action(parse.json).async { implicit request =>
    val userResult = request.body.validate[User]
    userResult.fold(
      errors => {
        Future.successful(BadRequest(Json.obj("error" -> "Invalid Json")))
      },
      userData => {
        repo
          .update(userData.withId(id))
          .map { user =>
            Ok(Json.toJson(user))
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
          Ok(Json.obj("status" -> s"user $id deleted"))
      }
  }

  val form = Form(
    mapping("id" -> ignored(None: Option[Long]), "nick" -> nonEmptyText)(
      User.apply,
    )(User.unapply),
  )

  def listForm(
  ) = messagesAction.async { implicit request: MessagesRequest[AnyContent] =>
    repo
      .findAll()
      .map { users =>
        Ok(listView(users))
      }
  }

  def saveForm(
  ) = messagesAction.async { implicit request: MessagesRequest[AnyContent] =>
    val formValidationResult = form.bindFromRequest()
    formValidationResult.fold(
      { formWithErrors: Form[User] =>
        Future.successful(BadRequest(editView(formWithErrors)))
      },
      { data: User =>
        repo
          .save(data.copy(id = None))
          .map { _ =>
            Redirect(routes.UserController.listForm())
              .flashing("info" -> "User added!")
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
      { formWithErrors: Form[User] =>
        Future.successful(BadRequest(editView(formWithErrors, id)))
      },
      { data: User =>
        repo
          .update(data.withId(id))
          .map { _ =>
            Redirect(routes.UserController.listForm())
              .flashing("info" -> "User modified!")
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
        Redirect(routes.UserController.listForm())
          .flashing("info" -> "User deleted!")
      }
  }

}
