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

case class StageOver(stageClear: Boolean,
                     temps: Long,
                     score: Long,
                     yellowBonusTot: Long,
                     redBonusTot: Long,
                     yellowBonusUsed: Long,
                     redBonusUsed: Long)

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
                nbYellowBonus: Long,
                nbRedBonus: Long,
                userId: Long,
                actualStage: Long)

case class UserStatistic(idStat: Option[Long],
                         totYellowBonusUsed: Long,
                         totRedBonusUsed: Long,
                         bestScore: Long,
                         maxLevel: Long,
                         averageLevel: Long,
                         averageScore: Long,
                         userId: Long)


case class CountryStat(countryName: String,
                       nbPlayer: Long,
                       bestScore: Long)

case class UserStatisticBrief(userName: String,
                              bestScore: Long,
                              maxLevel: Long)

case class Score(score: Long,
                 date: Timestamp)


case class PersonalStatistic(totYellowBonusUsed: Long,
                             totRedBonusUsed: Long,
                             bestScore: Long,
                             maxLevel: Long,
                             averageLevel: Long,
                             averageScore: Long)

case class NextStage(stageLevel: Long,
                     map: Array[Array[Int]])

case class ResumeGame(score: Long,
                      yellowBonus: Long,
                      redBonus: Long)