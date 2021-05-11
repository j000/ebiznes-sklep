package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import models.CollectionModel._
import scala.concurrent.{ ExecutionContext, Future }
import play.api.data.Forms._
import play.api.data.Form
import utils.DBImplicits

@Singleton
class CollectionController @Inject() (
  repo: CollectionRepository,
  messagesAction: MessagesActionBuilder,
  dbExecuter: DBImplicits,
)(
  implicit
  ec: ExecutionContext,
) extends InjectedController {
  import dbExecuter.executeOperation
  import views.html.collection._

  def index() = Action.async {
    repo
      .findAll()
      .map { collections =>
        Ok(Json.toJson(collections))
      }
  }

  def create() = Action(parse.json).async { implicit request =>
    request
      .body
      .validate[Collection]
      .fold(
        problems => Future(BadRequest("Invalid json content")),
        input => {
          repo
            .save(input.copy(id = None))
            .map { collection =>
              Ok(Json.toJson(collection))
            }
        },
      )
  }

  def read(id: Long) = Action.async {
    repo
      .findOne(id)
      .map {
        case Some(collection) =>
          Ok(Json.toJson(collection))
        case _ =>
          NotFound(Json.obj("error" -> "Not Found"))
      }
  }

  def update(id: Long) = Action(parse.json).async { implicit request =>
    val collectionResult = request.body.validate[Collection]
    collectionResult.fold(
      errors => {
        Future(BadRequest(Json.obj("error" -> "Invalid Json")))
      },
      collectionData => {
        repo
          .update(collectionData.withId(id))
          .map { collection =>
            Ok(Json.toJson(collection))
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
          Ok(s"collection $id deleted")
      }
  }

  val form = Form(
    mapping("id" -> ignored(None: Option[Long]), "name" -> nonEmptyText)(
      (id, name) => Collection(id, name)
    )((_: Collection) match { case Collection(id, name, _) => Some((id, name))})
  )

  def listForm(
  ) = messagesAction.async { implicit request: MessagesRequest[AnyContent] =>
    repo
      .findAll()
      .map { collections =>
        Ok(listView(collections))
      }
  }

  def saveForm(
  ) = messagesAction.async { implicit request: MessagesRequest[AnyContent] =>
    val formValidationResult = form.bindFromRequest()
    formValidationResult.fold(
      { formWithErrors: Form[Collection] =>
        Future(BadRequest(addView(formWithErrors)))
      },
      { data: Collection =>
        repo
          .save(data.copy(id = None))
          .map { _ =>
            Redirect(routes.CollectionController.listForm())
              .flashing("info" -> "Collection added!")
          }
      },
    )
  }

  def createForm(
  ) = messagesAction.async { implicit request: MessagesRequest[AnyContent] =>
    Future(Ok(addView(form)))
  }

  def updateForm(
    id: Long,
  ) = messagesAction.async { implicit request: MessagesRequest[AnyContent] =>
    val formValidationResult = form.bindFromRequest()
    formValidationResult.fold(
      { formWithErrors: Form[Collection] =>
        Future(BadRequest(editView(id, formWithErrors)))
      },
      { data: Collection =>
        repo
          .update(data.withId(id))
          .map { _ =>
            Redirect(routes.CollectionController.listForm())
              .flashing("info" -> "Collection modified!")
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
          Ok(editView(id, form.fill(data)))
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
        Redirect(routes.CollectionController.listForm())
          .flashing("info" -> "Collection deleted!")
      }
  }

}
