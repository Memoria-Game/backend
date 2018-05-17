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
                 nbOfRedBonus: Long,
                 nbOfYellowBonus: Long,
                 nbOfWalls: Long,
                 nbOfRows: Long,
                 nbOfCols: Long,
                 timeOfDisplay: Long)

case class Stage_Game(idGame: Long,
                      idStage: Long,
                      scoreStage: Long,
                      timeAtStage: Long,
                      RedBonusUsed: Long,
                      YellowBonusUsed: Long,
                      RedBonusTaken: Long,
                      YellowBonusTaken: Long)

case class Game(idGame: Option[Long],
                score: Long, date: Timestamp,
                isOver: Boolean,
                nbLifes: Int,
                nbYellowBonus: Int,
                userId: Long)

