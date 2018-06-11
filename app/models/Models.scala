package models

import java.sql.Timestamp

case class User(id: Option[Long],
                pseudo: String,
                email: String,
                password: String,
                country: String)


case class Friends(idUser1: Option[Long],
                   idUser2: Option[Long],
                   friendsSince: Timestamp)

case class Stage(levelNumber: Option[Long],
                 nbOfRedBonus: Int,
                 nbOfYellowBonus: Int,
                 nbOfWalls: Int,
                 nbOfRows: Int,
                 nbOfCols: Int,
                 timeOfDisplay: Double)

case class StageGame(idGame: Option[Long],
                     idStage: Option[Long],
                     scoreStage: Long,
                     timeAtStage: Long,
                     redBonusUsed: Long,
                     yellowBonusUsed: Long,
                     redBonusTaken: Long,
                     yellowBonusTaken: Long)

case class Game(idGame: Option[Long],
                score: Long,
                date: Timestamp,
                isOver: Boolean,
                nbLifes: Long,
                nbYellowBonus: Long,
                userId: Long)

case class UserStatistic(idStat: Option[Long],
                         totYellowBonusUsed: Long,
                         totRedBonusUsed: Long,
                         bestScore: Long,
                         maxLevel: Long,
                         averageLevel: Long,
                         averageScore: Long,
                         userId: Long)


case class CountryStat(contryName: String,
                       nbPlayer: Long,
                       bestScore: Long)

case class UserStatisticBrief(userName: String,
                              bestScore: Long,
                              maxStage: Long)

case class Score(score: Long,
                 date: Timestamp)

case class PersonalStatistic(scoreList: Seq[Score],
                             totYellowBonusUsed: Long,
                             totRedBonusUsed: Long,
                             bestScore: Long,
                             maxLevel: Long,
                             averageLevel: Long,
                             averageScore: Long)

