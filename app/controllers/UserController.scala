package controllers

import dao.UserDAO
import javax.inject.{Inject, Singleton}
import models.{FriendToAdd, SignIn, SignUp, User}
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.Writes._
import play.api.libs.json._
import play.api.mvc.{AbstractController, ControllerComponents}
import services.ConnexionService

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class UserController @Inject()(cc: ControllerComponents, connexionService: ConnexionService, userDAO: UserDAO, cs: ConnexionService) extends AbstractController(cc) {
  def validateJson[A: Reads] = parse.json.validate(
    _.validate[A].asEither.left.map(e => BadRequest(JsError.toJson(e)))
  )

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



  def welcome = Action.async { request =>
    cs.getUser(request).map(_ match {
      case None => Unauthorized("T'es pas connecté")
      case Some(u) => Ok("Salut mon brave " + u.pseudo + " !")
    })
  }


  def signin = Action.async(validateJson[SignIn]) { request =>
    val s = request.body
    cs.signin(s.pseudo, s.pwd).map(_ match {
      case None => Forbidden("Nom d'utilisateur ou mot de passe incorrecte")
      case Some(u) => Ok("Vous êtes bien connecté " + u.pseudo).withSession(request.session + ("user_id" -> u.id.get.toString))
    })
  }

  /*
  def signin = Action.async { request =>
    cs.connect()
    cs.isAllowed(request).map(_ match {
      case false => Unauthorized("")
      case true => Ok("").withSession(request.session + ("connected" -> ""))
    })
  }//
  */

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
    val userId = 1
    val friend = connexionService.getUser(f.pseudo)

    friend.map{
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

  /*
  // Refer to the StudentsController class in order to have more explanations.
  implicit val courseToJson: Writes[Course] = (
    (JsPath \ "id").write[Option[Long]] and
    (JsPath \ "name").write[String] and
    (JsPath \ "description").write[String] and
    (JsPath \ "hasApero").writeNullable[Boolean]
  )(unlift(Course.unapply))

  implicit val jsonToCourse: Reads[Course] = (
    (JsPath \ "id").readNullable[Long] and
    (JsPath \ "name").read[String](minLength[String](3) keepAnd maxLength[String](5)) and
    (JsPath \ "description").read[String] and
    (JsPath \ "hasApero").readNullable[Boolean]
  )(Course.apply _)

  def validateJson[A : Reads] = parse.json.validate(
    _.validate[A].asEither.left.map(e => BadRequest(JsError.toJson(e)))
  )

  def getCourses = Action.async {
    val coursesList = coursesDAO.list()
    coursesList map (c => Ok(Json.toJson(c)))
  }

  def createCourse = Action.async(validateJson[Course]) { request =>
    val course = request.body
    val createdCourse = coursesDAO.insert(course)

    createdCourse.map(c =>
      Ok(
        Json.obj(
          "status" -> "OK",
          "id" -> c.id,
          "message" -> ("Course '" + c.name + "' saved.")
        )
      )
    )
  }

  def getCourse(courseId: Long) = Action.async {
    val optionalCourse = coursesDAO.findById(courseId)

    optionalCourse.map {
      case Some(c) => Ok(Json.toJson(c))
      case None =>
        NotFound(Json.obj(
          "status" -> "Not Found",
          "message" -> ("Course #" + courseId + " not found.")
        ))
    }
  }

  def updateCourse(courseId: Long) = Action.async(validateJson[Course]) { request =>
    val newCourse = request.body

    coursesDAO.update(courseId, newCourse).map {
      case 1 => Ok(
        Json.obj(
          "status" -> "OK",
          "message" -> ("Course '" + newCourse.name + "' updated.")
        )
      )
      case 0 => NotFound(Json.obj(
        "status" -> "Not Found",
        "message" -> ("Course #" + courseId + " not found.")
      ))
    }
  }

  def deleteCourse(courseId: Long) = Action.async {
    coursesDAO.delete(courseId).map {
      case 1 => Ok(
        Json.obj(
          "status"  -> "OK",
          "message" -> ("Course #" + courseId + " deleted.")
        )
      )
      case 0 => NotFound(Json.obj(
        "status" -> "Not Found",
        "message" -> ("Course #" + courseId + " not found.")
      ))
    }
  }
  */
}
