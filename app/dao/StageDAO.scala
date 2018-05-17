package dao

import javax.inject.{Inject, Singleton}
import models.Stage
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

// We use a trait component here in order to share the StudentsTable class with other DAO, thanks to the inheritance.
trait StageComponent extends CoursesComponent with StudentsComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  // This class convert the database's students table in a object-oriented entity: the Student model.
  class StageTable(tag: Tag) extends Table[Stage](tag, "STAGE") {
    def levelNumber = column[Long]("LEVELNUMBER", O.PrimaryKey, O.AutoInc) // Primary key, auto-incremented
    def nbOfRedBonus = column[Int]("NBOFREDBONUS")
    def nbOfYellowBonus = column[Int]("NBOFYELLOWBONUS")
    def nbOfWalls = column[Int]("NBOFWALLS")
    def nbOfRows = column[Int]("NBOFROWS")
    def nbOfCols = column[Int]("NBOFCOLS")
    def timeOfDisplay = column[Double]("TIMEOFDISPLAY")

    // Map the attributes with the model; the ID is optional.
    def * = (levelNumber, nbOfRedBonus, nbOfYellowBonus, nbOfWalls, nbOfRows, nbOfCols, timeOfDisplay) <> (Stage.tupled, Stage.unapply)
  }
}

@Singleton
class StageDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends StageComponent with HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._

}
