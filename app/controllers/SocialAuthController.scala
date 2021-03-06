package controllers

import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.impl.providers._
import javax.inject.Inject
import play.api.mvc.{ Action, AnyContent, Cookie, Request }
import play.filters.csrf.CSRF.Token
import play.filters.csrf.{ CSRF, CSRFAddToken }
import scala.concurrent.{ ExecutionContext, Future }
import models.UserModel.User
import play.api.libs.json.Json
import utils.DBImplicits

class SocialAuthController @Inject() (
  scc: DefaultSilhouetteControllerComponents,
  addToken: CSRFAddToken,
  dbExecutor: DBImplicits,
)(
  implicit
  ex: ExecutionContext,
) extends SilhouetteController(scc) {
  import dbExecutor.executeOperation

  def authenticate(provider: String): Action[AnyContent] = addToken(
    Action.async { implicit request: Request[AnyContent] =>
      (
        socialProviderRegistry.get[SocialProvider](provider) match {
          case Some(p: SocialProvider with CommonSocialProfileBuilder) =>
            p.authenticate()
              .flatMap {
                case Left(result) =>
                  Future.successful(result)
                case Right(authInfo) =>
                  for {
                    profile <- p.retrieveProfile(authInfo)
                    futureUser <- userRepository
                      .retrieve(profile.loginInfo)
                      .map {
                        case Some(user) =>
                          Future.successful(user)
                        case None =>
                          executeOperation(
                            userRepository.save(
                              User(
                                None,
                                "",
                                profile.loginInfo.providerID,
                                profile.loginInfo.providerKey,
                                profile.email.getOrElse(""),
                              ),
                            ),
                          )
                      }
                    user <- futureUser
                    _ <- authInfoRepository.save(profile.loginInfo, authInfo)
                    authenticator <- authenticatorService
                      .create(profile.loginInfo)
                    value <- authenticatorService.init(authenticator)
                    result <- authenticatorService.embed(
                      value,
                      Redirect("https://ebiznes.azurewebsites.net/"),
                    )
                  } yield {
                    val Token(name, value) = CSRF.getToken.get
                    result.withCookies(
                      Cookie(name, value, httpOnly = false),
                      Cookie("profile", user.email, httpOnly = false),
                    )
                  }
              }
          case _ =>
            Future.failed(
              new ProviderException(
                s"Cannot authenticate with unexpected social provider $provider",
              ),
            )
        }
      ).recover { case e: ProviderException =>
        logger.error("Unexpected provider error", e)
        Forbidden(Json.obj("error" -> "Forbidden"))
      }
    },
  )

}
