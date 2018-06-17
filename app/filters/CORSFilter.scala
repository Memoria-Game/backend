package filters

import akka.stream.Materializer
import javax.inject.Inject
import play.api.http.HeaderNames
import play.api.mvc._
import services.ConnexionService

import scala.concurrent.{ExecutionContext, Future}

class CORSFilter @Inject()(implicit val mat: Materializer, ec: ExecutionContext, cs: ConnexionService) extends Filter {

  def apply(nextFilter: RequestHeader => Future[Result])
           (requestHeader: RequestHeader): Future[Result] = {

    nextFilter(requestHeader).map(_.withHeaders(HeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN -> requestHeader.headers.get("Origin").getOrElse("http://memoria.cf"),
      HeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS -> "true"))

  }
}