package controllers

import javax.inject._
import play.api.mvc.{Action, AnyContent}
import play.api.mvc.{AbstractController, ControllerComponents}

@Singleton
class PaymentController @Inject()(val cc: ControllerComponents)
extends AbstractController(cc)
{
  def index(): Action[AnyContent] = Action {
    Ok(s"payment list")
  }
  def show(id: Long): Action[AnyContent] = Action {
    Ok(s"payment $id")
  }
  def create(): Action[AnyContent] = Action {
    Ok(s"payment created");
  }
  def update(id: Long): Action[AnyContent] = Action {
    Ok(s"payment $id updated")
  }
  def delete(id: Long): Action[AnyContent] = Action {
    Ok(s"payment $id updated")
  }
}
