package controllers

import javax.inject._
import play.api.mvc.{Action, AnyContent}
import play.api.mvc.{AbstractController, ControllerComponents}

@Singleton
class FavouriteController @Inject()(val cc: ControllerComponents)
extends AbstractController(cc)
{
  def index(): Action[AnyContent] = Action {
    Ok(s"favourite list")
  }
  def show(id: Long): Action[AnyContent] = Action {
    Ok(s"favourite $id")
  }
  def create(): Action[AnyContent] = Action {
    Ok(s"favourite created");
  }
  def update(id: Long): Action[AnyContent] = Action {
    Ok(s"favourite $id updated")
  }
  def delete(id: Long): Action[AnyContent] = Action {
    Ok(s"favourite $id deleted")
  }
}
