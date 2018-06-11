package controllers

import java.sql.Timestamp

import dao.{GameDAO, UserDAO, UserStatisticDAO}
import javax.inject._
import models._
import play.api.libs.functional.syntax._
import play.api.libs.json.Writes._
import play.api.libs.json._
import scala.util.{Failure, Success}
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class StatisticsController @Inject()(cc: ControllerComponents, gameDAO: GameDAO, userDAO: UserDAO, userStatisticDAO: UserStatisticDAO) extends AbstractController(cc) {

  implicit val CountryStatToJson: Writes[CountryStat] = (
    (JsPath \ "contryName").write[String] and
      (JsPath \ "nbPlayer").write[Long] and
      (JsPath \ "bestScore").write[Long]
    ) (unlift(CountryStat.unapply))

  implicit val UserStatToJson: Writes[UserStatisticBrief] = (
    (JsPath \ "userName").write[String] and
      (JsPath \ "bestScore").write[Long] and
      (JsPath \ "maxLevel").write[Long]
    ) (unlift(UserStatisticBrief.unapply))

  implicit val ScoreToJson: Writes[Score] = (
    (JsPath \ "score").write[Long] and
      (JsPath \ "date").write[Timestamp]
    ) (unlift(Score.unapply))

  implicit val PersonalStatToJson: Writes[PersonalStatistic] = (
    (JsPath \ "scoreList").write[Seq[Score]] and
      (JsPath \ "totYellowBonusUsed").write[Long] and
      (JsPath \ "totRedBonusUsed").write[Long] and
      (JsPath \ "bestScore").write[Long] and
      (JsPath \ "maxLevel").write[Long] and
      (JsPath \ "averageLevel").write[Long] and
      (JsPath \ "averageScore").write[Long]
    ) (unlift(PersonalStatistic.unapply))

  def getStatByCountries = {
    NotImplemented(Json.obj(
      "status" -> "NotImplemented",
      "Message" -> "Pas encore implémenté"
    ))
  }

  def getStatByCountry(contryId: Long) =
    NotImplemented(Json.obj(
      "status" -> "NotImplemented",
      "Message" -> "Pas encore implémenté"
    ))


  def getStatFromHomeCountry = NotImplemented(Json.obj(
    "status" -> "NotImplemented",
    "Message" -> "Pas encore implémenté"
  ))

  def getStatFriends = NotImplemented(Json.obj(
    "status" -> "NotImplemented",
    "Message" -> "Pas encore implémenté"
  ))

  def getPersonnalStats = Action.async {
    val userID = 1
    val optionalUserStatistic = userStatisticDAO.getAllStatsFromUser(userID)
    val optionalGame = gameDAO.getAllGameOfUser(userID)

    var tes: Seq[Score] = Seq()
    optionalGame.onComplete {
      case Success(seq) => tes = seq.map(g => Score(g.score, g.date))
      case Failure(_) => tes = Seq()
    }

    optionalUserStatistic.map {
      case Some(s) => Ok(Json.toJson(userStatToPersonal(s, tes)))
      case None =>
        // Send back a 404 Not Found HTTP status to the client if the student does not exist.
        NotFound(Json.obj(
          "status" -> "Not Found",
          "message" -> ("UserStatistic  #" + userID + " not found.")
        ))
    }
  }

  def userStatToPersonal(us: UserStatistic, scoreList: Seq[Score]): PersonalStatistic =
    PersonalStatistic(scoreList, us.totYellowBonusUsed, us.totRedBonusUsed, us.bestScore, us.maxLevel, us.averageLevel, us.averageScore)
}