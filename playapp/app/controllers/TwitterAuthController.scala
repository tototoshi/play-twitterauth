package controllers

import play.api._
import play.api.libs.oauth._
import play.api.mvc._
import com.github.tototoshi.play2.twitterauth._

object TwitterAuthController extends TwitterAuthController {

  val loginSuccessURL = routes.Application.index

  val loginDeniedURL = routes.Application.index

  val logoutURL = routes.Application.index

  def onAuthorizationSuccess(request: Request[AnyContent], token: RequestToken, user: TwitterUser): Result = {
    Logger.info("login success")
    redirectToLoginSuccessURL(request, token, user)
  }

}
