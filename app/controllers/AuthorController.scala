package controllers

import javax.inject._
import play.api.mvc.{Action, AnyContent}
import play.api.mvc.{AbstractController, ControllerComponents}

@Singleton
class AuthorController @Inject()(val cc: ControllerComponents)
extends AbstractController(cc)
{
  def index(): Action[AnyContent] = Action {
    Ok(s"author list")
  }
  def show(id: Long): Action[AnyContent] = Action {
    Ok(s"author $id")
  }
  def create(): Action[AnyContent] = Action {
    Ok(s"author created");
  }
  def update(id: Long): Action[AnyContent] = Action {
    Ok(s"author $id updated")
  }
  def delete(id: Long): Action[AnyContent] = Action {
    Ok(s"author $id deleted")
  }
}
