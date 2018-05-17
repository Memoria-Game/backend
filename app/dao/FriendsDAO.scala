package dao

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
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc) // Primary key, auto-incremented
    def firstName = column[String]("FIRSTNAME")
    def lastName = column[String]("LASTNAME")
    def age = column[Int]("AGE")
    def isInsolent = column[Boolean]("ISINSOLENT")

    // Map the attributes with the model; the ID is optional.
    def * = (id.?, firstName, lastName, age, isInsolent) <> (Student.tupled, Student.unapply)
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
