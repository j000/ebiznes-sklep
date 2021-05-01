package controllers

import javax.inject._
import play.api.mvc.{Action, AnyContent}
import play.api.mvc.{AbstractController, ControllerComponents}

@Singleton
class UserController @Inject()(val cc: ControllerComponents)
extends AbstractController(cc)
{
  def index(): Action[AnyContent] = Action {
    Ok(s"user list")
  }
  def read(id: Long): Action[AnyContent] = Action {
    Ok(s"user $id")
  }
  def create(): Action[AnyContent] = Action {
    Ok(s"user created");
  }
  def update(id: Long): Action[AnyContent] = Action {
    Ok(s"user $id updated")
  }
  def delete(id: Long): Action[AnyContent] = Action {
    Ok(s"user $id updated")
  }
}
