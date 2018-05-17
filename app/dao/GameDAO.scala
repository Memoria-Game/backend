package dao

import java.sql.Timestamp

import scala.concurrent.Future
import javax.inject.{Inject, Singleton}
import models.Game
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

trait GameComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  // This class convert the database's courses table in a object-oriented entity: the Course model.
  class GameTable(tag: Tag) extends Table[Game](tag, "GAMES") {
    def id = column[Long]("IDGAME", O.PrimaryKey, O.AutoInc) // Primary key, auto-incremented
    def score = column[Long]("SCORE")
    def date = column[Timestamp]("DATE")
    def isOver = column[Boolean]("ISOVER")
    def nbLifes = column[Int]("NBLIFES")
    def nbYellowBonus = column[Int]("NBYELLOWBONUS")
    def userId = column[Long]("USERID")

    // Map the attributes with the model; the ID is optional.
    def * = (id.?, score, date, isOver, nbLifes, nbYellowBonus, userId) <> (Game.tupled, Game.unapply)
  }

}

// This class contains the object-oriented list of courses and offers methods to query the data.
// A DatabaseConfigProvider is injected through dependency injection; it provides a Slick type bundling a database and
// driver. The class extends the courses' query table and loads the JDBC profile configured in the application's
// configuration file.
@Singleton
class GameDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends GameComponent with HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._

}