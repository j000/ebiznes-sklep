package controllers

import javax.inject._
import play.api.mvc.{Action, AnyContent}
import play.api.mvc.{InjectedController, ControllerComponents}
import play.api.libs.json._
import play.api.mvc.Request
import models.{User,UserRepository}
import models.UserJson._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserController @Inject()(
  val repo: UserRepository,
)(
  implicit ec: ExecutionContext
) extends InjectedController
{
  type UserDBO = repo.UserDBO

  def index() = Action.async {
    repo.index().map { users =>
      Ok(Json.toJson(users))
    }
  }
  def create() = Action(parse.json).async { request =>
    request.body.validate[User].fold(
      problems => {
        Future(BadRequest("Invalid json content"))
      },
      input => {
        repo.create(input).map {
          user => Ok(Json.toJson(user))
        }
      }
    )
  }
  def read(id: Long) = Action.async {
    repo.read(id).map {
      case Some(user) => Ok(Json.toJson(user))
      case _ => NotFound(Json.obj("error" -> "Not Found"))
    }
  }
  def update(id: Long) = Action(parse.json).async { implicit request =>
    val userResult = request.body.validate[User]
    userResult.fold(
      errors => {
        Future(BadRequest(Json.obj("error" -> "Invalid Json")))
      },
      userData => {
        repo.update(id, userData).map {
          case None => NotFound(Json.obj("error" -> "Not Found"))
          case user => Ok(Json.toJson(user))
        }
      }
    )
  }
  def delete(id: Long) = Action.async {
    repo.delete(id).map {
      case 0 => NotFound(Json.obj("error" -> "Not Found"))
      case _ => Ok(s"user $id deleted")
    }
  }
}
