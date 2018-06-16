package services

import dao._
import javax.inject.Inject
import models._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class StatisticService @Inject()(friendsDATO: FriendsDAO, gameDAO: GameDAO, userDAO: UserDAO, userStatisticDAO: UserStatisticDAO, stageGameDAO: StageGameDAO) {

  def UpdateBonusTot(userId: Long, so: StageOver) = {
    val us = Await.result(userStatisticDAO.getStatsFromUser(userId), Duration.Inf)

    userStatisticDAO.updateStat(UserStatistic(Option(us.userId),
      us.totYellowBonusUsed + so.yellowBonusUsed,
      us.totRedBonusUsed + so.redBonusUsed,
      us.bestScore,
      us.maxLevel,
      us.averageLevel,
      us.averageScore,
      us.userId))
  }

  def gameOver(g: Game) = {
    val us = Await.result(userStatisticDAO.getStatsFromUser(g.userId), Duration.Inf)

    val totGameUser = Await.result(gameDAO.getNumberOfGameOfUser(g.userId), Duration.Inf)

    userStatisticDAO.updateStat(UserStatistic(Option(us.userId),
      us.totYellowBonusUsed,
      us.totRedBonusUsed,
      g.score,
      g.actualStage,
      (us.averageLevel * (totGameUser - 1) + g.actualStage) / totGameUser,
      (us.averageScore * (totGameUser - 1) + g.score) / totGameUser,
      us.userId))
  }
}
