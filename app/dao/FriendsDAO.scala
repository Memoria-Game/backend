package dao

import java.sql.Timestamp

import javax.inject.{Inject, Singleton}
import models.Friends
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

    def pk = primaryKey("primaryKey",(idUser1, idUser2))

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
  extends FriendsComponent with HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._


}
