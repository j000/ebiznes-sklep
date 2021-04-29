package controllers

import javax.inject._
import play.api.mvc.{Action, AnyContent}
import play.api.mvc.{AbstractController, ControllerComponents}

@Singleton
class OrderController @Inject()(val cc: ControllerComponents)
extends AbstractController(cc)
{
  def index(): Action[AnyContent] = Action {
    Ok(s"order list")
  }
  def show(id: Long): Action[AnyContent] = Action {
    Ok(s"order $id")
  }
  def create(): Action[AnyContent] = Action {
    Ok(s"order created");
  }
  def update(id: Long): Action[AnyContent] = Action {
    Ok(s"order $id updated")
  }
  def delete(id: Long): Action[AnyContent] = Action {
    Ok(s"order $id deleted")
  }
}
