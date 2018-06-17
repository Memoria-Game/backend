package controllers

import java.sql.Timestamp

import dao.{FriendsDAO, GameDAO, UserDAO, UserStatisticDAO}
import javax.inject._
import models._
import play.api.libs.functional.syntax._
import play.api.libs.json.Writes._
import play.api.libs.json._
import play.api.mvc.{AbstractController, ControllerComponents}
import services.{ConnexionService, StatisticService}

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class StatisticController @Inject()(cc: ControllerComponents, connexionService: ConnexionService, friendsDAO: FriendsDAO,
                                    gameDAO: GameDAO, statisticService: StatisticService, userDAO: UserDAO,
                                    userStatisticDAO: UserStatisticDAO)
  extends AbstractController(cc) {

  implicit val CountryStatToJson: Writes[CountryStat] = (
    (JsPath \ "countryName").write[String] and
      (JsPath \ "nbPlayer").write[Long] and
      (JsPath \ "bestScore").write[Long]
    ) (unlift(CountryStat.unapply))

  implicit val UserStatisticBriefToJson: Writes[UserStatisticBrief] = (
    (JsPath \ "userName").write[String] and
      (JsPath \ "bestScore").write[Long] and
      (JsPath \ "maxLevel").write[Long]
    ) (unlift(UserStatisticBrief.unapply))

  implicit val ScoreToJson: Writes[Score] = (
    (JsPath \ "score").write[Long] and
      (JsPath \ "date").write[Timestamp]
    ) (unlift(Score.unapply))

  implicit val PersonalStatToJson: Writes[PersonalStatistic] = (
    (JsPath \ "totYellowBonusUsed").write[Long] and
      (JsPath \ "totRedBonusUsed").write[Long] and
      (JsPath \ "bestScore").write[Long] and
      (JsPath \ "maxLevel").write[Long] and
      (JsPath \ "averageLevel").write[Long] and
      (JsPath \ "averageScore").write[Long]
    ) (unlift(PersonalStatistic.unapply))


  def getStatByCountries = Action.async {
    val countryStat = statisticService.getAllCountryStat

    countryStat map (cs => Ok(Json.toJson(cs)))
  }

  def getStatFromHomeCountry = Action.async { request =>
    val userId = connexionService.getUserId(request)
    val homeCountryStat = statisticService.getHomeCountryStat(userId)

    homeCountryStat.map(s => Ok(Json.toJson(s)))
  }

  def getStatFriends = Action.async { request =>
    val userId = connexionService.getUserId(request)
    val friendsStats = friendsDAO.getFriendsStats(userId)

    friendsStats.map { fs => Ok(Json.toJson(fs)) }
  }

  def getPersonalStats = Action.async { request =>
    val userId = connexionService.getUserId(request)

    userStatisticDAO.getStatsFromUser(userId)
      .map(us => userStatToPersonal(us))
      .map(ps => Ok(Json.toJson(ps)))

  }

  def getPersonalScores = Action.async { request =>
    val userId = connexionService.getUserId(request)

    gameDAO.getAllGameOfUser(userId).map(seq => Ok(Json.toJson(seq.map(g => Score(g.score, g.date)))))
  }

  def userStatToPersonal(us: UserStatistic): PersonalStatistic =
    PersonalStatistic(us.totYellowBonusUsed, us.totRedBonusUsed, us.bestScore, us.maxLevel, us.averageLevel, us.averageScore)
}