package controllers

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.impl.providers._
import controllers.request.SignUpRequest
import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.{ Action, AnyContent, Request }
import models.UserModel.User
import scala.concurrent.{ ExecutionContext, Future }
import scala.language.postfixOps
import utils.DBImplicits

class SignUpController @Inject() (
  components: DefaultSilhouetteControllerComponents,
  dbExecutor: DBImplicits,
)(
  implicit
  ex: ExecutionContext,
) extends SilhouetteController(components) {
  import dbExecutor.executeOperation

  def signUp: Action[AnyContent] = unsecuredAction.async {
    implicit request: Request[AnyContent] =>
      val json = request.body.asJson.get
      val signUpRequest = json.as[SignUpRequest]
      val loginInfo = LoginInfo(CredentialsProvider.ID, signUpRequest.email)

      userRepository
        .retrieve(loginInfo)
        .flatMap {
          case Some(_) =>
            Future.successful(Forbidden("User already exists"))
          case None =>
            val authInfo = passwordHasherRegistry
              .current
              .hash(signUpRequest.password)
            executeOperation(
              userRepository.save(
                User(
                  None,
                  "",
                  CredentialsProvider.ID,
                  signUpRequest.email,
                  signUpRequest.email,
                ),
              ),
            ).flatMap { user =>
                authInfoRepository.add(loginInfo, authInfo).map(_ => user)
              }
              .map { user =>
                Json.toJson(user)
              }
              .map { json =>
                Created(json)
              }
        }
  }

}
