package controllers

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.AuthenticatorResult
import models.UserModel.User
import play.api.mvc._
import play.api.libs.json._

import scala.concurrent.{ ExecutionContext, Future }

abstract class AbstractAuthController(
  scc: DefaultSilhouetteControllerComponents,
)(
  implicit
  ex: ExecutionContext,
) extends SilhouetteController(scc) {

  protected def authenticateUser(
    user: User,
  )(
    implicit
    request: RequestHeader,
  ): Future[AuthenticatorResult] = {
    authenticatorService
      .create(LoginInfo(user.providerId, user.providerKey))
      .flatMap { authenticator =>
        authenticatorService
          .init(authenticator)
          .flatMap { v =>
            authenticatorService.embed(v, Ok(Json.toJson(user)))
          }
      }
  }

}
