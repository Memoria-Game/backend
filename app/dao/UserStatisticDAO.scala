
package dao

import javax.inject.{Inject, Singleton}
import models.UserStatistic
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

// We use a trait component here in order to share the User class with other DAO, thanks to the inheritance.
trait UserStatisticComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  // This class convert the database's students table in a object-oriented entity: the Student model.
  class UsersStatisticTable(tag: Tag) extends Table[UserStatistic](tag, "USER_STATISTIC") {
    def id = column[Long]("IDUSERSTATISTIC", O.PrimaryKey, O.AutoInc) // Primary key, auto-incremented
    def totYellowBonusUsed = column[Long]("TOTYELLOWBONUSUSED")

    def totRedBonusUsed = column[Long]("TOTREDBONUSUSED")

    def bestScore = column[Long]("BESTSCORE")

    def maxLevel = column[Long]("MAXLEVEL")

    def averageLevel = column[Long]("AVERAGELEVEL")

    def averageScore = column[Long]("AVERAGESCORE")

    def userId = column[Long]("USERID")

    // Map the attributes with the model; the ID is optional.
    def * = (id.?, totYellowBonusUsed, totRedBonusUsed, bestScore, maxLevel, averageLevel, averageScore, userId) <> (UserStatistic.tupled, UserStatistic.unapply)
  }

}

// This class contains the object-oriented list of students and offers methods to query the data.
// A DatabaseConfigProvider is injected through dependency injection; it provides a Slick type bundling a database and
// driver. The class extends the students' query table and loads the JDBC profile configured in the application's
// configuration file.
@Singleton
class UserStatisticDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends UserStatisticComponent with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  val userStatistics = TableQuery[UsersStatisticTable]

  def getStatsFromUser(userId: Long): Future[Option[UserStatistic]] = {
    val query = userStatistics.filter(_.userId === userId)
    db.run(query.result.headOption)
  }

  def getAllStats(): Future[Seq[UserStatistic]] =
    db.run(userStatistics.result)

}
