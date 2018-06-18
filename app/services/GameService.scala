package services

import dao._
import javax.inject.Inject
import models._
import utils.LevelGenerator

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class GameService @Inject()(friendsDATO: FriendsDAO, gameDAO: GameDAO, stageDAO: StageDAO, stageGameDAO: StageGameDAO) {
  def createNewStage(stageId: Long): Array[Array[Int]] = {
    val s = Await.result(stageDAO.getStage(stageId), Duration.Inf)

    val levelGenerator = new LevelGenerator(s.nbOfRows,
      s.nbOfCols,
      s.nbOfYellowBonus,
      s.nbOfRedBonus,
      s.nbOfWalls)

    levelGenerator.generateLevel()
  }

  def updateGameAfterStage(g: Game, so: StageOver) = {
    gameDAO.updateGame(Game(g.idGame,
      g.score + so.score,
      g.date,
      g.isOver,
      so.yellowBonusTot,
      so.redBonusTot,
      g.userId,
      g.actualStage))
  }

  def endStage(g: Game, so: StageOver) = {
    val optionSG = Await.result(stageGameDAO.getStageGame(g.idGame.get, g.actualStage), Duration.Inf)
    stageGameDAO.updateStageGame(StageGame(g.idGame,optionSG.idStage, so.score, so.temps, so.redBonusUsed,
      so.yellowBonusUsed, so.redBonusTot - g.nbRedBonus - so.redBonusUsed,
      so.yellowBonusTot - g.nbYellowBonus - so.yellowBonusUsed))
  }

  def stageComplete(g: Game) = {
    gameDAO.updateActualStage(g.idGame.get, g.actualStage + 1)
    stageGameDAO.createStageGame(g.idGame.get, g.actualStage + 1)
  }

  def getInfoForResume(g: Game): ResumeGame = ResumeGame(g.score, g.nbYellowBonus, g.nbRedBonus)

  def createNewGame(userId: Long) = {
    gameDAO.createGame(userId)
    stageGameDAO.createStageGame(userId, 1)
  }

  def winGame(u: User) = {

  }

  def looseGame(u: User) = {

  }
}