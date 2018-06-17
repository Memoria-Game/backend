import akka.stream.Materializer
import javax.inject.Inject
import play.api.mvc._
import services.ConnexionService

import scala.concurrent.{ExecutionContext, Future}

class AuthFilter @Inject()(implicit val mat: Materializer, ec: ExecutionContext, cs: ConnexionService) extends Filter {

  def apply(nextFilter: RequestHeader => Future[Result])
           (requestHeader: RequestHeader): Future[Result] = {

    val authorizedEndpoint = Array("/signin", "/signup")

    requestHeader.session.get("user_id").nonEmpty || authorizedEndpoint.contains(requestHeader.path) || requestHeader.method == "OPTIONS" match {
      case false => Future {
        Results.Unauthorized("You have to be connected to access this page")
      }
      case true => nextFilter(requestHeader)
    }

  }
}