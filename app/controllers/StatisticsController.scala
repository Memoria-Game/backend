package controllers

import dao.UserDAO

import concurrent._
import javax.inject.{Inject, Singleton}
import models.User
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.mvc.{AbstractController, ControllerComponents}

import ExecutionContext.Implicits.global

@Singleton
class StatisticsController @Inject()(cc: ControllerComponents, userDAO: UserDAO) extends AbstractController(cc) {

  implicit val userToJson: Writes[User] = (
    (JsPath \ "idUser").write[Option[Long]] and
      (JsPath \ "pseudo").write[String] and
      (JsPath \ "email").write[String] and
      (JsPath \ "password").write[String] and
      (JsPath \ "country").write[String]
    ) (unlift(User.unapply))

  def welcome = Action.async {
    val users = userDAO.list

    users.map(u => Ok(Json.toJson(u)))
  }
}