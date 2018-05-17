package dao

import javax.inject.{Inject, Singleton}
import models.{StageGame}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

// We use a trait component here in order to share the StudentsTable class with other DAO, thanks to the inheritance.
trait StageGameComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  // This class convert the database's students table in a object-oriented entity: the Student model.
  class StageGameTable(tag: Tag) extends Table[StageGame](tag, "STAGE_GAME") {
    def idGame = column[Long]("IDGAME", O.PrimaryKey) // Primary key, auto-incremented
    def idStage = column[Long]("IDSTAGE", O.PrimaryKey) // Primary key, auto-incremented
    def scoreStage = column[Long]("SCORESTAGE")
    def timeAtStage = column[Long]("TIMEATSTAGE")
    def redBonusUsed = column[Long]("REDBONUSUSED")
    def yellowBonusUsed = column[Long]("YELLOWBONUSUSED")
    def redBonusTaken = column[Long]("REDBONUSTAKEN")
    def yellowBonusTaken = column[Long]("YELLOWBONUSTAKEN")

    // Map the attributes with the model; the ID is optional.
    def * = (idGame, idStage, scoreStage, timeAtStage, redBonusUsed, yellowBonusUsed, redBonusTaken, redBonusTaken) <> (StageGame.tupled, StageGame.unapply)
  }
}

// This class contains the object-oriented list of students and offers methods to query the data.
// A DatabaseConfigProvider is injected through dependency injection; it provides a Slick type bundling a database and
// driver. The class extends the students' query table and loads the JDBC profile configured in the application's
// configuration file.
@Singleton
class GameStageDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends StageGameComponent with HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._

  // Get the object-oriented list of students directly from the query table.
  val students = TableQuery[StageGame]

}
