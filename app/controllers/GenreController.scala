package controllers

import javax.inject._
import play.api.mvc.{Action, AnyContent}
import play.api.mvc.{AbstractController, ControllerComponents}

@Singleton
class GenreController @Inject()(val cc: ControllerComponents)
extends AbstractController(cc)
{
  def index(): Action[AnyContent] = Action {
    Ok(s"genre list")
  }
  def show(id: Long): Action[AnyContent] = Action {
    Ok(s"genre $id")
  }
  def create(): Action[AnyContent] = Action {
    Ok(s"genre created");
  }
  def update(id: Long): Action[AnyContent] = Action {
    Ok(s"genre $id updated")
  }
  def delete(id: Long): Action[AnyContent] = Action {
    Ok(s"genre $id deleted")
  }
}
