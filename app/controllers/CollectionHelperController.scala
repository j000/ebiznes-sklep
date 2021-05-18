package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import models.CollectionHelperModel._
import scala.concurrent.{ ExecutionContext, Future }
import play.api.data.Forms._
import play.api.data.Form
import utils.DBImplicits

@Singleton
class CollectionHelperController @Inject() (
  repo: CollectionHelperRepository,
  messagesAction: MessagesActionBuilder,
  dbExecuter: DBImplicits,
)(
  implicit
  ec: ExecutionContext,
) extends InjectedController {
  import dbExecuter.executeOperation
  import views.html.collectionhelper._

  def index() = Action.async {
    repo
      .findAll()
      .map { collectionhelpers =>
        Ok(Json.toJson(collectionhelpers))
      }
  }

  def create() = Action(parse.json).async { implicit request =>
    request
      .body
      .validate[CollectionHelper]
      .fold(
        problems => Future(BadRequest("Invalid json content")),
        input => {
          repo
            .save(input.copy(id = None))
            .map { collectionhelper =>
              Ok(Json.toJson(collectionhelper))
            }
        },
      )
  }

  def read(id: Long) = Action.async {
    repo
      .findOne(id)
      .map {
        case Some(collectionhelper) =>
          Ok(Json.toJson(collectionhelper))
        case _ =>
          NotFound(Json.obj("error" -> "Not Found"))
      }
  }

  def update(id: Long) = Action(parse.json).async { implicit request =>
    val collectionhelperResult = request.body.validate[CollectionHelper]
    collectionhelperResult.fold(
      errors => {
        Future(BadRequest(Json.obj("error" -> "Invalid Json")))
      },
      collectionhelperData => {
        repo
          .update(collectionhelperData.withId(id))
          .map { collectionhelper =>
            Ok(Json.toJson(collectionhelper))
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
          Ok(Json.obj("status" -> s"collectionhelper $id deleted"))
      }
  }

  val form = Form(
    mapping(
      "id" -> ignored(None: Option[Long]),
      "collection_id" -> longNumber,
      "book_id" -> longNumber,
    )(CollectionHelper.apply)(CollectionHelper.unapply),
  )

  def listForm(
  ) = messagesAction.async { implicit request: MessagesRequest[AnyContent] =>
    repo
      .findAll()
      .map { collectionhelpers =>
        Ok(listView(collectionhelpers))
      }
  }

  def saveForm(
  ) = messagesAction.async { implicit request: MessagesRequest[AnyContent] =>
    val formValidationResult = form.bindFromRequest()
    formValidationResult.fold(
      { formWithErrors: Form[CollectionHelper] =>
        Future(BadRequest(editView(formWithErrors)))
      },
      { data: CollectionHelper =>
        repo
          .save(data.copy(id = None))
          .map { _ =>
            Redirect(routes.CollectionHelperController.listForm())
              .flashing("info" -> "CollectionHelper added!")
          }
      },
    )
  }

  def createForm(
  ) = messagesAction.async { implicit request: MessagesRequest[AnyContent] =>
    Future(Ok(editView(form)))
  }

  def updateForm(
    id: Long,
  ) = messagesAction.async { implicit request: MessagesRequest[AnyContent] =>
    val formValidationResult = form.bindFromRequest()
    formValidationResult.fold(
      { formWithErrors: Form[CollectionHelper] =>
        Future(BadRequest(editView(formWithErrors, id)))
      },
      { data: CollectionHelper =>
        repo
          .update(data.withId(id))
          .map { _ =>
            Redirect(routes.CollectionHelperController.listForm())
              .flashing("info" -> "CollectionHelper modified!")
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
        Redirect(routes.CollectionHelperController.listForm())
          .flashing("info" -> "CollectionHelper deleted!")
      }
  }

}
