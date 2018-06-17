package controllers

import java.sql.Timestamp

import dao.GameDAO
import javax.inject._
import models.{NextStage, _}
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.mvc.{AbstractController, ControllerComponents}
import services.{ConnexionService, GameService, StatisticService}

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class GameController @Inject()(cc: ControllerComponents, connexionService: ConnexionService, gameDAO: GameDAO,
                               gameService: GameService, statisticService: StatisticService)
  extends AbstractController(cc) {

  implicit val NextStageToJson: Writes[NextStage] = (
    (JsPath \ "stageLevel").write[Long] and
      (JsPath \ "map").write[Array[Array[Int]]]
    ) (unlift(NextStage.unapply))

  implicit val ResumeGameToJson: Writes[ResumeGame] = (
    (JsPath \ "score").write[Long] and
      (JsPath \ "yellowBonus").write[Long] and
      (JsPath \ "redBonus").write[Long]
    ) (unlift(ResumeGame.unapply))

  implicit val GameToJson: Writes[Game] = (
    (JsPath \ "idGame").write[Option[Long]] and
      (JsPath \ "score").write[Long] and
      (JsPath \ "date").write[Timestamp] and
      (JsPath \ "isOver").write[Boolean] and
      (JsPath \ "nbYellowBonus").write[Long] and
      (JsPath \ "nbRedBonus").write[Long] and
      (JsPath \ "userId").write[Long] and
      (JsPath \ "actualStage").write[Long]
    ) (unlift(Game.unapply))

  implicit val jsonToStageOver: Reads[StageOver] = (
    (JsPath \ "stageClear").read[Boolean] and
      (JsPath \ "temps").read[Long] and
      (JsPath \ "score").read[Long] and
      (JsPath \ "yellowBonusTot").read[Long] and
      (JsPath \ "redBonusTot").read[Long] and
      (JsPath \ "yellowBonusUsed").read[Long] and
      (JsPath \ "redBonusUsed").read[Long]
    ) (StageOver.apply _)

  /**
    * This helper parses and validates JSON using the implicit `jsonToStageOver` above, returning errors if the parsed
    * json fails validation.
    */
  def validateJson[A: Reads] = parse.json.validate(
    _.validate[A].asEither.left.map(e => BadRequest(JsError.toJson(e)))
  )


  def getGame(userId: Long) = Action.async {
    val optionalGame = gameDAO.getAllGameOfUser(userId)

    optionalGame map (s => Ok(Json.toJson(s)))
  }

  def endStage = Action.async(validateJson[StageOver]) { request =>
    val stageOver = request.body
    val userId = connexionService.getUserId(request)
    val optionalGame = gameDAO.getCurrentGameOfUser(userId)

    optionalGame.map {
      case Some(g) => {
        gameService.updateGameAfterStage(g, stageOver)
        gameService.endStage(g, stageOver)
        statisticService.UpdateBonusTot(userId, stageOver)

        stageOver.stageClear match {
          case false => {
            gameDAO.gameOver(g.idGame.get)
            statisticService.gameOver(g)
            Ok(Json.obj(
              "status" -> "OK",
              "message" -> ("Game Over")
            ))
          }
          case true => {
            gameService.stageComplete(g)
            Ok(Json.obj(
              "status" -> "OK",
              "message" -> ("Ready to getNext Stage")
            ))
          }
        }
      }
      case None => NotFound(Json.obj( // Send back a 404 Not Found HTTP status to the client if the student does not exist.
        "status" -> "Not Found",
        "message" -> ("No game running for user " + userId)
      ))
    }
  }

  def nextStage = Action.async { request =>
    val userId = connexionService.getUserId(request)
    val optionalGame = gameDAO.getCurrentGameOfUser(userId)

    optionalGame map {
      case Some(g) => {
        val map = gameService.createNewStage(g.actualStage)

        Ok(Json.toJson(NextStage(g.actualStage, map)))
      }
      case None => NotFound(Json.obj( // Send back a 404 Not Found HTTP status to the client if the student does not exist.
        "status" -> "Not Found",
        "message" -> "No game running, to create one use /game/resume"
      ))
    }
  }

  def resume = Action.async { implicit request =>
    val userId = connexionService.getUserId(request)
    val optionalGame = gameDAO.getCurrentGameOfUser(userId)

    optionalGame.map {
      case Some(g) => {
        val gameResumed: ResumeGame = gameService.getInfoForResume(g)
        Ok(Json.toJson(gameResumed))
      }
      case None => {
        gameService.createNewGame(userId)

        Ok(Json.toJson(ResumeGame(0, 0, 0)))
      }
    }
  }
}
