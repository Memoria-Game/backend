package controllers

import dao.UserDAO
import javax.inject.{Inject, Singleton}
import models._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.Writes._
import play.api.libs.json._
import play.api.mvc.{AbstractController, ControllerComponents}
import services.ConnexionService

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class UserController @Inject()(cc: ControllerComponents, connexionService: ConnexionService, userDAO: UserDAO,
                               cs: ConnexionService)
  extends AbstractController(cc) {

  implicit val gamToJson: Writes[User] = (
    (JsPath \ "idUser").write[Option[Long]] and
      (JsPath \ "pseudo").write[String] and
      (JsPath \ "email").write[String] and
      (JsPath \ "password").write[String] and
      (JsPath \ "country").write[String]
    ) (unlift(User.unapply))

  implicit val jsonToSignIn: Reads[SignIn] = (
    (JsPath \ "pseudo").read[String] and
      (JsPath \ "pwd").read[String]
    ) (SignIn.apply _)

  implicit val jsonToFriendToAdd: Reads[FriendToAdd] = (
    (__ \ "pseudo").read[String]).map(pseudo => FriendToAdd(pseudo))

  implicit val jsonToSignUp: Reads[SignUp] = (
    (JsPath \ "pseudo").read[String] and
      (JsPath \ "email").read[String] and
      (JsPath \ "pwd").read[String] and
      (JsPath \ "country").read[String]
    ) (SignUp.apply _)

  def validateJson[A: Reads] = parse.json.validate(
    _.validate[A].asEither.left.map(e => BadRequest(JsError.toJson(e)))
  )

  def welcome = Action.async { request =>
    cs.getUser(request).map(u => Ok("Salut mon brave " + u.get.pseudo + " !"))
  }


  def signin = Action.async(validateJson[SignIn]) { request =>
    val s = request.body

    cs.signin(s.pseudo, s.pwd).map(_ match {
      case None => Forbidden("Nom d'utilisateur ou mot de passe incorrecte")
      case Some(u) => Ok("Vous êtes bien connecté " + u.pseudo).withSession(request.session + ("user_id" -> u.id.get.toString))
    })
  }

  // {"pseudo": Dream, "email": "basile.ch@htomail.ch", "pwd": "1234", "country":switzerland }
  def signup = Action.async(validateJson[SignUp]) { request =>
    val s = request.body

    cs.signup(s.pseudo, s.pwd, s.email, s.country).map(_ match {
      case None => Forbidden("Cet utilisateur existe déjà")
      case Some(u) => Ok("Bienvenue" + u.pseudo).withSession(request.session + ("user_id" -> u.id.get.toString))
    })
  }

  def logout = Action {
    Ok("Bien déconnecté").withNewSession
  }

  def addFriend = Action.async(validateJson[FriendToAdd]) { request =>
    val f = request.body
    val userId = connexionService.getUserId(request)
    val friend = connexionService.getUser(f.pseudo)

    friend.map {
      case Some(f) => {
        connexionService.addFriend(userId, f.id.get)
        Ok(Json.obj(
          "status" -> "Ok",
          "message" -> ("Add User \"" + f.pseudo + "\" as a friend.")
        ))
      }
      case None => NotFound(Json.obj(
        "status" -> "Not Found",
        "message" -> ("User \"" + f.pseudo + "\" not found.")
      ))
    }
  }
}
