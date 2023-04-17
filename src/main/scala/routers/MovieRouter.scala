package routers

import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import routers.MovieRouter.moviesPath
import services.MovieListServiceInterface
import utils.BaseRouter

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext

class MovieRouter(movieListService: MovieListServiceInterface)
                 (implicit val ec: ExecutionContext)
  extends BaseRouter {

  def routes: Route = path(moviesPath) {
    (get
      & parameters("from_time".as[LocalDateTime], "to_time".as[LocalDateTime])
      & pathEndOrSingleSlash) { (fromTime, toTime) =>
        complete(movieListService.listMovieScreenings(fromTime, toTime))
    }
  }
}

object MovieRouter {
  val moviesPath = "movies"
}
