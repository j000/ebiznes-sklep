package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import models.ReviewModel._
import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Failure, Success }
import play.api.data.Forms._
import play.api.data.Form
import utils.DBImplicits

@Singleton
class ReviewController @Inject() (
  repo: ReviewRepository,
  messagesAction: MessagesActionBuilder,
  dbExecuter: DBImplicits,
)(
  implicit
  ec: ExecutionContext,
) extends InjectedController {
  import dbExecuter.executeOperation
  import views.html.review._

  def index() = Action.async {
    repo
      .findAll()
      .map { reviews =>
        Ok(Json.toJson(reviews))
      }
  }

  def create() = Action(parse.json).async { implicit request =>
    request
      .body
      .validate[Review]
      .fold(
        problems => Future(BadRequest("Invalid json content")),
        input => {
          repo
            .save(input.copy(id = None))
            .map { review =>
              Ok(Json.toJson(review))
            }
        },
      )
  }

  def read(id: Long) = Action.async {
    repo
      .findOne(id)
      .map {
        case Some(review) =>
          Ok(Json.toJson(review))
        case _ =>
          NotFound(Json.obj("error" -> "Not Found"))
      }
  }

  def update(id: Long) = Action(parse.json).async { implicit request =>
    val reviewResult = request.body.validate[Review]
    reviewResult.fold(
      errors => {
        Future(BadRequest(Json.obj("error" -> "Invalid Json")))
      },
      reviewData => {
        repo
          .update(reviewData.withId(id))
          .map { review =>
            Ok(Json.toJson(review))
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
          Ok(Json.obj("status" -> s"review $id deleted"))
      }
  }

  val form = Form(
    mapping(
      "id" -> ignored(None: Option[Long]),
      "content" -> nonEmptyText,
      "user_id" -> longNumber,
      "book_id" -> longNumber,
    )(
      Review.apply,
    )(Review.unapply),
  )

  def listForm(
  ) = messagesAction.async { implicit request: MessagesRequest[AnyContent] =>
    repo
      .findAll()
      .map { reviews =>
        Ok(listView(reviews))
      }
  }

  def saveForm(
  ) = messagesAction.async { implicit request: MessagesRequest[AnyContent] =>
    val formValidationResult = form.bindFromRequest()
    formValidationResult.fold(
      { formWithErrors: Form[Review] =>
        Future(BadRequest(addView(formWithErrors)))
      },
      { data: Review =>
        repo
          .save(data.copy(id = None))
          .map { _ =>
            Redirect(routes.ReviewController.listForm())
              .flashing("info" -> "Review added!")
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
      { formWithErrors: Form[Review] =>
        Future(BadRequest(editView(id, formWithErrors)))
      },
      { data: Review =>
        repo
          .update(data.withId(id))
          .map { _ =>
            Redirect(routes.ReviewController.listForm())
              .flashing("info" -> "Review modified!")
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
        Redirect(routes.ReviewController.listForm())
          .flashing("info" -> "Review deleted!")
      }
  }

}
