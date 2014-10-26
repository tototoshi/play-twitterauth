package com.github.tototoshi.play2.twitterauth

import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.oauth._

trait TwitterAuthController extends Controller {

  protected val loginSuccessURL: play.api.mvc.Call

  protected val loginDeniedURL: play.api.mvc.Call

  protected val logoutURL: play.api.mvc.Call

  def login = Action { implicit request =>
    TwitterAuth.getAuthorizedUser(request) match {
      case Some(_) => Redirect(loginSuccessURL)
      case None => TwitterAuth.oauth.retrieveRequestToken(TwitterAuthConfiguration.oauthCallbackURL) match {
        case Right(token) =>
          Redirect(TwitterAuth.oauth.redirectUrl(token.token)).withSession(
            request.session +
              ("twitter.requestTokenSecret" -> token.secret)
          )
        case Left(e) => {
          Logger.error(e.getMessage)
          InternalServerError(e.getMessage)
        }
      }
    }
  }

  def authorize = Action { implicit request =>
    val form = Form(
      tuple(
        "oauth_token" -> optional(nonEmptyText),
        "oauth_verifier" -> optional(nonEmptyText),
        "denied" -> optional(nonEmptyText)
      )
    )

    form.bindFromRequest.fold({
      formWithError => BadRequest
    }, {
      case (Some(oauthToken), Some(oauthVerifier), None) =>
        (for {
          tokenSecret <- request.session.get("twitter.requestTokenSecret")
          requestToken = RequestToken(oauthToken, tokenSecret)
          token <- TwitterAuth.oauth.retrieveAccessToken(
            requestToken, oauthVerifier
          ).right.toOption
        } yield {
          val user = TwitterAuth.getAuthorizedUserWithAccessToken(token.token, token.secret)
          onAuthorizationSuccess(request, token, user)
        }).getOrElse(BadRequest)
      case (None, None, Some(denied)) => onDenied(request, denied)
      case _ => BadRequest
    })
  }

  def onDenied(request: Request[AnyContent], denied: String): Result =
    Redirect(loginDeniedURL)

  def logout = Action { implicit request =>
    Redirect(logoutURL).withSession(
      request.session
        - "twitter.id"
        - "twitter.screenName"
        - "twitter.name"
        - "twitter.description"
        - "twitter.profileImageURL"
    )
  }

  protected def onAuthorizationSuccess(request: Request[AnyContent], token: RequestToken, user: TwitterUser): Result

  protected def redirectToLoginSuccessURL[A](
    request: Request[A],
    token: RequestToken,
    user: TwitterUser): Result = {
    Redirect(
      request.session
        .get("twitter.auth.redirect.url")
        .getOrElse(loginSuccessURL.url)
    ).withSession(
        request.session
          - "twitter.auth.redirect.url"
          + ("twitter.id" -> user.id.toString)
          + ("twitter.screenName" -> user.screenName)
          + ("twitter.name" -> user.name)
          + ("twitter.description" -> user.description)
          + ("twitter.profileImageURL" -> user.profileImageURL)
      )
  }
}
