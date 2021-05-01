package controllers

import javax.inject._
import play.api.mvc.{Action, AnyContent}
import play.api.mvc.{AbstractController, ControllerComponents}

@Singleton
class CollectionController @Inject()(val cc: ControllerComponents)
extends AbstractController(cc)
{
  def index(): Action[AnyContent] = Action {
    Ok(s"collection list")
  }
  def read(id: Long): Action[AnyContent] = Action {
    Ok(s"collection $id")
  }
  def create(): Action[AnyContent] = Action {
    Ok(s"collection created");
  }
  def update(id: Long): Action[AnyContent] = Action {
    Ok(s"collection $id updated")
  }
  def delete(id: Long): Action[AnyContent] = Action {
    Ok(s"collection $id deleted")
  }
}
