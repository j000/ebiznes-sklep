package controllers

import javax.inject._
import play.api.mvc.{Action, AnyContent}
import play.api.mvc.{AbstractController, ControllerComponents}

@Singleton
class BasketController @Inject()(val cc: ControllerComponents)
extends AbstractController(cc)
{
  def index(): Action[AnyContent] = Action {
    Ok(s"basket list")
  }
  def show(id: Long): Action[AnyContent] = Action {
    Ok(s"basket $id")
  }
  def create(): Action[AnyContent] = Action {
    Ok(s"basket created");
  }
  def update(id: Long): Action[AnyContent] = Action {
    Ok(s"basket $id updated")
  }
  def delete(id: Long): Action[AnyContent] = Action {
    Ok(s"basket $id deleted")
  }
}
