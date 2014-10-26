package controllers

import play.api._
import play.api.mvc._
import com.github.tototoshi.play2.twitterauth._

object Application extends Controller {

  def index = Action { implicit request =>
    Ok(views.html.index(TwitterAuth.getAuthorizedUser(request)))
  }

}
