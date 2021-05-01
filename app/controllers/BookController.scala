package controllers

import javax.inject._
import play.api.mvc.{Action, AnyContent}
import play.api.mvc.{AbstractController, ControllerComponents}

@Singleton
class BookController @Inject()(val cc: ControllerComponents)
extends AbstractController(cc)
{
  def index(): Action[AnyContent] = Action {
    Ok(s"book list")
  }
  def read(id: Long): Action[AnyContent] = Action {
    Ok(s"book $id")
  }
  def create(): Action[AnyContent] = Action {
    Ok(s"book created");
  }
  def update(id: Long): Action[AnyContent] = Action {
    Ok(s"book $id updated")
  }
  def delete(id: Long): Action[AnyContent] = Action {
    Ok(s"book $id deleted")
  }
}
