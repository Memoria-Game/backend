package dao

import java.sql.Timestamp

import javax.inject.{Inject, Singleton}
import models.{Friends, UserStatisticBrief}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

// We use a trait component here in order to share the Friends class with other DAO, thanks to the inheritance.
trait FriendsComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  // This class convert the database's students table in a object-oriented entity: the Student model.
  class FriendsTable(tag: Tag) extends Table[Friends](tag, "FRIENDS") {
    def idUser1 = column[Long]("IDUSER1") // Primary key, auto-incremented
    def idUser2 = column[Long]("IDUSER2")

    def friendsSince = column[Timestamp]("FRIENDSSINCE")

    def pk = primaryKey("primaryKey", (idUser1, idUser2))

    // Map the attributes with the model; the ID is optional.
    def * = (idUser1.?, idUser2.?, friendsSince) <> (Friends.tupled, Friends.unapply)

  }

}

// This class contains the object-oriented list of students and offers methods to query the data.
// A DatabaseConfigProvider is injected through dependency injection; it provides a Slick type bundling a database and
// driver. The class extends the students' query table and loads the JDBC profile configured in the application's
// configuration file.
@Singleton
class FriendsDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends FriendsComponent with UserComponent with UserStatisticComponent with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  val friends = TableQuery[FriendsTable]
  val users = TableQuery[UsersTable]
  val statistics = TableQuery[UsersStatisticTable]

  def getFriendsId(): Future[Seq[Long]] = {
    val userId: Long = 1
    val query = friends.filter(_.idUser1 === userId).map(_.idUser2)
    db.run(query.result)
  }

  def addFriend(idUser: Long, idFriend: Long): Future[Int] = {
    val insertQuery = friends.map(f => (f.idUser1, f.idUser2))

    db.run(insertQuery += (idUser, idFriend))
  }

  def getFriendsStats(userId: Long): Future[Seq[UserStatisticBrief]] = {

    val query = for {
      friend <- friends.filter(_.idUser1 === userId).map(_.idUser2)
      userName <- users.filter(_.id === friend).map(_.pseudo)
      friendStat <- statistics.filter(_.userId === friend)
    } yield (userName, friendStat.bestScore, friendStat.maxLevel)

    db.run(query.result).map(seq => seq.map(item => UserStatisticBrief(item._1, item._2, item._3)))
  }
}
