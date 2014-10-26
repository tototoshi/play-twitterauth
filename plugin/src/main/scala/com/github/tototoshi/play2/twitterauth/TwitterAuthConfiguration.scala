package com.github.tototoshi.play2.twitterauth

import play.api.Play.current

object TwitterAuthConfiguration {

  lazy val consumerKey = current.configuration.getString("twitter.consumerKey").getOrElse(
    sys.error("twitter.consumerKey is missing")
  )

  lazy val consumerSecret = current.configuration.getString("twitter.consumerSecret").getOrElse(
    sys.error("twitter.consumerSecret is missing")
  )

  lazy val oauthCallbackURL = current.configuration.getString("twitter.callbackURL").getOrElse(
    sys.error("twitter.callbackURL is missing")
  )

}
