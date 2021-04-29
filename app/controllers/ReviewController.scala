package controllers

import javax.inject._
import play.api.mvc.{Action, AnyContent}
import play.api.mvc.{AbstractController, ControllerComponents}

@Singleton
class ReviewController @Inject()(val cc: ControllerComponents)
extends AbstractController(cc)
{
  def index(): Action[AnyContent] = Action {
    Ok(s"review list")
  }
  def show(id: Long): Action[AnyContent] = Action {
    Ok(s"review $id")
  }
  def create(): Action[AnyContent] = Action {
    Ok(s"review created");
  }
  def update(id: Long): Action[AnyContent] = Action {
    Ok(s"review $id updated")
  }
  def delete(id: Long): Action[AnyContent] = Action {
    Ok(s"review $id updated")
  }
}
