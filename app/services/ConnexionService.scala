package services

import dao.{FriendsDAO, UserDAO, UserStatisticDAO}
import javax.inject.Inject
import models.User
import play.api.mvc.Request

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}


class ConnexionService @Inject()(ud: UserDAO, friendsDAO: FriendsDAO, statisticDAO: UserStatisticDAO) {

  def signin(username: String, password: String): Future[Option[User]] = ud.getUser(username, password)

  def getUser(implicit request: Request[_]): Future[Option[User]] = getUser(request.session.get("user_id").getOrElse("-1").toLong)

  def getUser(username: String): Future[Option[User]] = ud.getUser(username)

  def getUser(id: Long): Future[Option[User]] = ud.getUser(id)

  def getUserId(implicit request: Request[_]) = request.session.get("user_id").get.toLong

  def exists(username: String): Future[Boolean] = {
    ud.getUser(username).map(_.nonEmpty)
  }

  def signup(username: String, password: String, mail: String, country: String): Future[Option[User]] = {
    exists(username).flatMap(_ match {
      case true => Future {
        None
      }
      case false => {
        val OptionalUser = ud.insertUser(username, password, mail, country).map(Some(_))
        val userId = Await.result(OptionalUser, Duration.Inf).get.id.get
        statisticDAO.createUserStat(userId)
        friendsDAO.addFriend(userId, userId)
        OptionalUser
      }
    })
  }

  def addFriend(userId: Long, friendId: Long) =
    friendsDAO.addFriend(userId, friendId)


  /*
  def checkUser(implicit request: Request[_], func:Future[User] => Result):Future[Result] = {
    getUser(request).map(_ match {
      case None => Results.Unauthorized("T'es pas connecté, idiot")
      case Some(u) => func(_)
    })
  }*/

  def isAllowed(implicit request: Request[_]) = {
    request.session.get("user_id").nonEmpty
  }

  /*
  def isAllowed(username:String, password:String):Future[Boolean] = {
    val user = ud.getUser(username)

    user.map(u => u.map(s => s.password == password).getOrElse(false))
  }
  def isAllowed(implicit request: Request[_]):Future[Boolean] = {
    val auth = getAuth(request)
    isAllowed(auth._1, auth._2)
  }
   def getAuth(implicit request: Request[_]) = {
    val authArray = request.headers.get("Authorization")
      .map(Base64.getDecoder.decode(_))
      .map(new String(_, StandardCharsets.UTF_8))
      .map(_.split(' ').last)
      .map(_.split(':'))
      .get

    (authArray(0), authArray(1))
  }*/

}
