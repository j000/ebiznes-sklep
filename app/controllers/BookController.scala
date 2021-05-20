package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import models.BookModel._
import models.AuthorModel._
import models.GenreModel._
import scala.concurrent.{ ExecutionContext, Future }
import play.api.data.Forms._
import play.api.data.Form
import utils.DBImplicits

@Singleton
class BookController @Inject() (
  repo: BookRepository,
  messagesAction: MessagesActionBuilder,
  dbExecuter: DBImplicits,
  authorRepo: AuthorRepository,
  genreRepo: GenreRepository,
)(
  implicit
  ec: ExecutionContext,
) extends InjectedController {
  import dbExecuter.executeOperation
  import views.html.book._

  def index() = Action.async {
    repo
      .findAll()
      .map { books =>
        Ok(Json.toJson(books))
      }
  }

  def create() = Action(parse.json).async { implicit request =>
    request
      .body
      .validate[Book]
      .fold(
        problems => Future.successful(BadRequest(Json.obj("error" -> "Invalid Json"))),
        input => {
          repo
            .save(input.copy(id = None))
            .map { book =>
              Ok(Json.toJson(book))
            }
        },
      )
  }

  def read(id: Long) = Action.async {
    repo
      .findOne(id)
      .map {
        case Some(book) =>
          Ok(Json.toJson(book))
        case _ =>
          NotFound(Json.obj("error" -> "Not Found"))
      }
  }

  def update(id: Long) = Action(parse.json).async { implicit request =>
    val bookResult = request.body.validate[Book]
    bookResult.fold(
      errors => {
        Future.successful(BadRequest(Json.obj("error" -> "Invalid Json")))
      },
      bookData => {
        repo
          .update(bookData.withId(id))
          .map { book =>
            Ok(Json.toJson(book))
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
          Ok(Json.obj("status" -> s"book $id deleted"))
      }
  }

  val form = Form(
    mapping(
      "id" -> ignored(None: Option[Long]),
      "title" -> nonEmptyText,
      "author_id" -> longNumber,
      "genre_id" -> longNumber,
      "price" -> longNumber,
    )(Book.apply)(Book.unapply),
  )

  def listForm(
  ) = messagesAction.async { implicit request: MessagesRequest[AnyContent] =>
    repo
      .findAll()
      .map { books =>
        Ok(listView(books))
      }
  }

  def saveForm(
  ) = messagesAction.async { implicit request: MessagesRequest[AnyContent] =>
    val formValidationResult = form.bindFromRequest()
    formValidationResult.fold(
      { formWithErrors: Form[Book] =>
        Future.successful(
          BadRequest(
            editView(
              formWithErrors,
              Seq[(String, String)](),
              Seq[(String, String)](),
            ),
          ),
        )
      },
      { data: Book =>
        repo
          .save(data.copy(id = None))
          .map { _ =>
            Redirect(routes.BookController.listForm())
              .flashing("info" -> "Book added!")
          }
      },
    )
  }

  def createForm(
  ) = messagesAction.async { implicit request: MessagesRequest[AnyContent] =>
    val authors: Future[Seq[Author]] = authorRepo.findAll()
    val genres: Future[Seq[Genre]] = genreRepo.findAll()

    authors.flatMap { authors =>
      val authorOptions = authors.map(a => (a.id.get.toString, a.name));
      genres.map { genres =>
        val genresOptions = genres.map(g => (g.id.get.toString, g.name));
        Ok(editView(form, authorOptions, genresOptions))
      }
    }
  }

  def updateForm(
    id: Long,
  ) = messagesAction.async { implicit request: MessagesRequest[AnyContent] =>
    form
      .bindFromRequest()
      .fold(
        { formWithErrors: Form[Book] =>
          val authors: Future[Seq[Author]] = authorRepo.findAll()
          val genres: Future[Seq[Genre]] = genreRepo.findAll()

          authors.flatMap { authors =>
            val authorOptions = authors.map(a => (a.id.get.toString, a.name));
            genres.map { genres =>
              val genresOptions = genres.map(g => (g.id.get.toString, g.name));
              BadRequest(
                editView(formWithErrors, authorOptions, genresOptions, id),
              )
            }
          }
        },
        { data: Book =>
          repo
            .update(data.withId(id))
            .map { _ =>
              Redirect(routes.BookController.listForm())
                .flashing("info" -> "Book modified!")
            }
        },
      )
  }

  def editForm(
    id: Long,
  ) = messagesAction.async { implicit request: MessagesRequest[AnyContent] =>
    val bookFromId: Future[Option[Book]] = repo.findOne(id)
    bookFromId.flatMap {
      case None =>
        Future.successful(NotFound("Wrong ID"))
      case Some(book) =>
        val authors: Future[Seq[Author]] = authorRepo.findAll()
        val genres: Future[Seq[Genre]] = genreRepo.findAll()

        authors.flatMap { authors =>
          val authorOptions = authors.map(a => (a.id.get.toString, a.name));
          genres.map { genres =>
            val genresOptions = genres.map(g => (g.id.get.toString, g.name));
            Ok(editView(form.fill(book), authorOptions, genresOptions, id))
          }
        }
    }
  }

  def deleteForm(
    id: Long,
  ) = Action.async {
    repo
      .delete(id)
      .map { _ =>
        Redirect(routes.BookController.listForm())
          .flashing("info" -> "Book deleted!")
      }
  }

}
