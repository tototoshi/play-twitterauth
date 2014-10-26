# play-twitterauth

## Setup

### build.sbt

```scala
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play" % play.core.PlayVersion.current,
  "com.typesafe.play" %% "play-ws" % play.core.PlayVersion.current,
  "org.twitter4j" % "twitter4j-core" % "4.0.2",
  "com.github.tototoshi" %% "play-twitterauth" % "0.1.0-SNAPSHOT"
)
```

### Writing controller for authorization

```
GET     /login                      controllers.TwitterAuthController.login
GET     /logout                     controllers.TwitterAuthController.logout
GET     /authorize                  controllers.TwitterAuthController.authorize
```

```scala
package controllers

import play.api._
import play.api.libs.oauth._
import play.api.mvc._
import com.github.tototoshi.play2.twitterauth._

object TwitterAuthController extends TwitterAuthController {

  val loginSuccessURL = routes.Application.index

  val loginDeniedURL = routes.Application.index

  val logoutURL = routes.Application.index

  def postLogin(request: Request[AnyContent], token: RequestToken, user: TwitterUser): Result = {
    // do something
    redirectToLoginSuccessURL(request, token, user)
  }

}
```


### Getting authorized user in action

You can simply get an authorized user with `TwitterAuth.getAuthorizedUser`

```scala
  def index = Action { implicit request =>
    import com.github.tototoshi.play2.twitterauth._
    var user: Option[TwitterUser] = TwitterAuth.getAuthorizedUser(request)

    // do something
  }
```

Writing a simple helper may be good.

```scala
package controllers

import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import com.github.tototoshi.play2.twitterauth._

object Authorized {

  def apply(request: Request[AnyContent])(f: TwitterUser => Result): Result = {
    TwitterAuth.getAuthorizedUser(request).map(f).getOrElse {
      Redirect(routes.TwitterAuthController.login)
        .withSession("twitter.auth.redirect.url" -> request.uri)
    }
  }

}

object Application extends Controller {

  def index = Action { implicit request =>
    Authorized(request) {
        // do something
        Ok("done!")
    }
  }

}
```
