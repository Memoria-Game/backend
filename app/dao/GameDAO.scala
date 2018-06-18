package dao

import java.sql.Timestamp

import javax.inject.{Inject, Singleton}
import models.Game
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}


trait GameComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  // This class convert the database's courses table in a object-oriented entity: the Course model.
  class GameTable(tag: Tag) extends Table[Game](tag, "GAME") {
    def idGame = column[Long]("IDGAME", O.PrimaryKey, O.AutoInc) // Primary key, auto-incremented

    def score = column[Long]("SCORE")

    def date = column[Timestamp]("DATE")

    def isOver = column[Boolean]("ISOVER")

    def nbYellowBonus = column[Long]("NBYELLOWBONUS")

    def nbRedBonus = column[Long]("NBREDBONUS")

    def userId = column[Long]("USERID")

    def actualStage = column[Long]("ACTUALSTAGE")

    // Map the attributes with the model; the ID is optional.
    def * = (idGame.?, score, date, isOver, nbYellowBonus, nbRedBonus, userId, actualStage) <> (Game.tupled, Game.unapply)
  }

}

// This class contains the object-oriented list of courses and offers methods to query the data.
// A DatabaseConfigProvider is injected through dependency injection; it provides a Slick type bundling a database and
// driver. The class extends the courses' query table and loads the JDBC profile configured in the application's
// configuration file.
@Singleton
class GameDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends GameComponent with UserComponent with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  val games = TableQuery[GameTable]
  val users = TableQuery[UsersTable]

  /* Récupère la partie en cours d'un utilisateur */
  def getCurrentGameOfUser(userId: Long): Future[Option[Game]] = {
    val query = games.filter(_.userId === userId).filter(_.isOver === false)

    db.run(query.result.headOption)
  }

  /* Récupère les parties terminées d'un utilisateur */
  def getAllGameDoneOfUser(userId: Long): Future[Seq[Game]] = {
    val query = games.filter(_.userId === userId).filter(_.isOver === true).sortBy(_.date)
    
    db.run(query.result)
  }

  /* Récupère les parties terminées d'un utilisateur */
  def getAllGameOfUser(userId: Long): Future[Seq[Game]] = {
    val query = games.filter(_.userId === userId).sortBy(_.date)
    db.run(query.result)
  }

  def getNumberOfGameOfUser(userId: Long): Future[Int] =
    getAllGameOfUser(userId).map(_.length)

  def createGame(userId: Long): Future[Game] = {
    val g = Game(None,
      0,
      new Timestamp(System.currentTimeMillis()),
      false,
      0,
      0,
      userId,
      1)

    val insertQuery = games returning games.map(_.idGame) into ((g, idGame) => g.copy(Some(idGame)))

    db.run(insertQuery += g)
  }

  def updateGame(game: Game): Future[Int] = {
    val updateQuery = games.filter(_.idGame === game.idGame).update(game)

    db.run(updateQuery)
  }

  def gameOver(gameId: Long) = {
    val updateQuery = games.filter(_.idGame === gameId).map(_.isOver).update(true)

    db.run(updateQuery)
  }

  def updateActualStage(gameId: Long, newActualLevel: Long) = {
    val updateQuery = games.filter(_.idGame === gameId).map(_.actualStage).update(newActualLevel)

    db.run(updateQuery)
  }
}