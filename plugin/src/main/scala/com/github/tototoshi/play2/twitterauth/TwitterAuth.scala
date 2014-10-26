package com.github.tototoshi.play2.twitterauth

import play.api._
import play.api.mvc._
import play.api.libs.oauth._

object TwitterAuth {

  private val twitterServiceInfo = {
    val twitterRequestTokenURL = "https://api.twitter.com/oauth/request_token"
    val twitterAccessTokenURL = "https://api.twitter.com/oauth/access_token"
    val twitterAuthorizationURL = "https://api.twitter.com/oauth/authorize"
    val twitterConsumerKey =
      ConsumerKey(
        TwitterAuthConfiguration.consumerKey,
        TwitterAuthConfiguration.consumerSecret
      )
    ServiceInfo(
      twitterRequestTokenURL,
      twitterAccessTokenURL,
      twitterAuthorizationURL,
      twitterConsumerKey
    )
  }

  lazy val oauth = OAuth(twitterServiceInfo, use10a = true)

  def getAuthorizedUser(request: RequestHeader): Option[TwitterUser] =
    for {
      id <- request.session.get("twitter.id")
      screenName <- request.session.get("twitter.screenName")
      name <- request.session.get("twitter.name")
      description <- request.session.get("twitter.description")
      profileImageURL <- request.session.get("twitter.profileImageURL")
    } yield {
      TwitterUser(id.toLong, screenName, name, description, profileImageURL)
    }

  def getAuthorizedUserWithAccessToken(accessToken: String, accessTokenSecret: String): TwitterUser = {
    import twitter4j._
    import twitter4j.conf._
    val twitter4jConfBuilder = new ConfigurationBuilder
    val twitter4jConf = twitter4jConfBuilder
      .setOAuthConsumerKey(TwitterAuthConfiguration.consumerKey)
      .setOAuthConsumerSecret(TwitterAuthConfiguration.consumerSecret)
      .setOAuthAccessToken(accessToken)
      .setOAuthAccessTokenSecret(accessTokenSecret)
      .build

    val twitter = new TwitterFactory(twitter4jConf).getInstance()
    val twitterId = twitter.getId
    val userInfo = twitter.showUser(twitterId)
    TwitterUser(
      twitter.getId,
      userInfo.getScreenName,
      userInfo.getName,
      userInfo.getDescription,
      userInfo.getProfileImageURL
    )
  }

}
