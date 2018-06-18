package services

import dao._
import javax.inject.Inject
import models._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class StatisticService @Inject()(connexionService: ConnexionService, friendsDATO: FriendsDAO, gameDAO: GameDAO,
                                 userDAO: UserDAO, userStatisticDAO: UserStatisticDAO, stageGameDAO: StageGameDAO) {

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

  def getAllCountryStat: Future[Seq[CountryStat]] = {
    val users = userDAO.getAllUsers()

    users.map(seq => {
      seq.map(u => (u.country, u.id.get))
        .map(elem => (elem._1, Await.result(userStatisticDAO.getStatsFromUser(elem._2), Duration.Inf).bestScore))
        .groupBy(_._1)
        .map { case (k, v) => (k, v.map(_._2)) }
        .toSeq
        .map(elem => CountryStat(elem._1, elem._2.length, elem._2.max))
    })
  }

  def getHomeCountryStat(userId: Long): Future[CountryStat] = {
    val optionalUser = connexionService.getUser(userId)
    val user = Await.result(optionalUser, Duration.Inf).get
    val usersSameCountry = userDAO.getUsersFromCountry(user.country)

    usersSameCountry.map(seq => {
      val listScore = seq.map(_.id.get)
        .map(id => Await.result(userStatisticDAO.getStatsFromUser(id), Duration.Inf).bestScore)

      CountryStat(user.country, listScore.length, listScore.max)
    })
  }

  def createStatUser(userId: Long) = userStatisticDAO.createUserStat(userId)

  def gameOver(g: Game) = {
    val us = Await.result(userStatisticDAO.getStatsFromUser(g.userId), Duration.Inf)
    val totGameUser = Await.result(gameDAO.getNumberOfGameOfUser(g.userId), Duration.Inf)
    val userStat = Await.result(userStatisticDAO.getStatsFromUser(g.userId), Duration.Inf)

    userStatisticDAO.updateStat(UserStatistic(Option(us.userId),
      us.totYellowBonusUsed,
      us.totRedBonusUsed,
      if (g.score > userStat.bestScore) g.score else userStat.bestScore,
      if (g.actualStage > userStat.maxLevel) g.actualStage else userStat.maxLevel,
      (us.averageLevel * (totGameUser - 1) + g.actualStage) / totGameUser,
      (us.averageScore * (totGameUser - 1) + g.score) / totGameUser,
      us.userId))
  }
}
