package controllers

import javax.inject.{ Inject, Singleton }
import play.api.mvc._
import play.api.libs.json._
import models.ProviderModel._
import scala.concurrent.{ ExecutionContext, Future }
import play.api.data.Forms._
import play.api.data.Form
import utils.DBImplicits

@Singleton
class ProviderController @Inject() (
  repo: ProviderRepository,
  messagesAction: MessagesActionBuilder,
  dbExecutor: DBImplicits,
)(
  implicit
  ec: ExecutionContext,
) extends InjectedController {
  import dbExecutor.executeOperation
  import views.html.provider._

  def index() = Action.async {
    repo
      .findAll()
      .map { providers =>
        Ok(Json.toJson(providers))
      }
  }

  def create() = Action(parse.json).async { implicit request =>
    request
      .body
      .validate[Provider]
      .fold(
        problems =>
          Future.successful(BadRequest(Json.obj("error" -> "Invalid Json"))),
        input => {
          repo
            .save(input.copy(id = None))
            .map { provider =>
              Ok(Json.toJson(provider))
            }
        },
      )
  }

  def read(id: Long) = Action.async {
    repo
      .findOne(id)
      .map {
        case Some(provider) =>
          Ok(Json.toJson(provider))
        case _ =>
          NotFound(Json.obj("error" -> "Not Found"))
      }
  }

  def update(id: Long) = Action(parse.json).async { implicit request =>
    val providerResult = request.body.validate[Provider]
    providerResult.fold(
      errors => {
        Future.successful(BadRequest(Json.obj("error" -> "Invalid Json")))
      },
      providerData => {
        repo
          .update(providerData.withId(id))
          .map { provider =>
            Ok(Json.toJson(provider))
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
          Ok(Json.obj("status" -> s"provider $id deleted"))
      }
  }

  val form = Form(
    mapping(
      "id" -> ignored(None: Option[Long]),
      "userID" -> longNumber,
      "providerID" -> nonEmptyText,
      "providerKey" -> nonEmptyText,
    )(Provider.apply)(Provider.unapply),
  )

  def listForm(
  ) = messagesAction.async { implicit request: MessagesRequest[AnyContent] =>
    repo
      .findAll()
      .map { providers =>
        Ok(listView(providers))
      }
  }

  def saveForm(
  ) = messagesAction.async { implicit request: MessagesRequest[AnyContent] =>
    val formValidationResult = form.bindFromRequest()
    formValidationResult.fold(
      { formWithErrors: Form[Provider] =>
        Future.successful(BadRequest(editView(formWithErrors)))
      },
      { data: Provider =>
        repo
          .save(data.copy(id = None))
          .map { _ =>
            Redirect(routes.ProviderController.listForm())
              .flashing("info" -> "Provider added!")
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
      { formWithErrors: Form[Provider] =>
        Future.successful(BadRequest(editView(formWithErrors, id)))
      },
      { data: Provider =>
        repo
          .update(data.withId(id))
          .map { _ =>
            Redirect(routes.ProviderController.listForm())
              .flashing("info" -> "Provider modified!")
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
        Redirect(routes.ProviderController.listForm())
          .flashing("info" -> "Provider deleted!")
      }
  }

}
