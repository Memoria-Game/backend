package models

import java.sql.Timestamp

case class User(idUser: Option[Long],
                pseudo: String,
                email: String,
                password: String,
                country: String)

case class Friends(idUser1: Long,
                   idUser2: Long,
                   friendsSince: Timestamp)

case class Stage(levelNumber: Option[Long],
                 nbOfRedBonus: Int,
                 nbOfYellowBonus: Int,
                 nbOfWalls: Int,
                 nbOfRows: Int,
                 nbOfCols: Int,
                 timeOfDisplay: Double)

case class StageGame(idGame: Long,
                      idStage: Long,
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
                nbLifes: Int,
                nbYellowBonus: Int,
                userId: Long)

