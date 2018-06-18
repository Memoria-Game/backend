package services

import java.security.MessageDigest

import dao.{FriendsDAO, UserDAO, UserStatisticDAO}
import javax.inject.Inject
import models.User
import play.api.mvc.Request

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}


class ConnexionService @Inject()(ud: UserDAO, friendsDAO: FriendsDAO, statisticDAO: UserStatisticDAO) {

  def signin(username: String, password: String): Future[Option[User]] = ud.getUser(username, hash(password))

  def getUser(implicit request: Request[_]): Future[Option[User]] = getUser(request.session.get("user_id").getOrElse("-1").toLong)

  def getUser(username: String): Future[Option[User]] = ud.getUser(username)

  def getUser(id: Long): Future[Option[User]] = ud.getUser(id)

  def getUserId(implicit request: Request[_]) = request.session.get("user_id").get.toLong

  def exists(username: String): Future[Boolean] = {
    ud.getUser(username).map(_.nonEmpty)
  }

  def signup(username: String, password: String, mail: String, country: String): Future[Option[User]] = {
    val hp = hash(password)
    exists(username).flatMap(_ match {
      case true => Future {
        None
      }
      case false => {
        val OptionalUser = ud.insertUser(username, hp, mail, country)
        val user = Await.result(OptionalUser, Duration.Inf)
        statisticDAO.createUserStat(user.id.get)
        OptionalUser.map(Some(_))
      }
    })
  }

  def addFriend(userId: Long, friendId: Long) =
    friendsDAO.addFriend(userId, friendId)

  def isAllowed(implicit request: Request[_]) = {
    request.session.get("user_id").nonEmpty
  }


  def hash(str:String) = {
    MessageDigest.getInstance("SHA-256")
      .digest(str.getBytes("UTF-8"))
      .map("%02x".format(_)).mkString
  }

}
