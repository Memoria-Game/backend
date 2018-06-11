package dao

import javax.inject.{Inject, Singleton}
import models.User
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

// We use a trait component here in order to share the User class with other DAO, thanks to the inheritance.
trait UserComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  // This class convert the database's students table in a object-oriented entity: the Student model.
  class UsersTable(tag: Tag) extends Table[User](tag, "USER") {
    def id = column[Long]("IDUSER", O.PrimaryKey, O.AutoInc) // Primary key, auto-incremented
    def pseudo = column[String]("PSEUDO")
    def email = column[String]("EMAIL")
    def password = column[String]("PASSWORD")
    def country = column[String]("COUNTRY")

    // Map the attributes with the model; the ID is optional.
    def * = (id.?, pseudo, email, password, country) <> (User.tupled, User.unapply)
  }
}

// This class contains the object-oriented list of students and offers methods to query the data.
// A DatabaseConfigProvider is injected through dependency injection; it provides a Slick type bundling a database and
// driver. The class extends the students' query table and loads the JDBC profile configured in the application's
// configuration file.
@Singleton
class UserDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends UserComponent with HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._

  val users = TableQuery[UsersTable]

  def list():Future[Seq[User]] = {
    val query = users
    db.run(query.result)
  }

  def getUserName(userId: Long): Future[Option[String]] = {
    val query = users.filter(_.id === userId).map(u => u.pseudo)
    db.run(query.result.headOption)
  }


}
