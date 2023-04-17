package routers

import akka.http.scaladsl.model.StatusCodes.{InternalServerError, NotFound}
import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import routers.ScreeningRouter.screeningsPath
import services.{ScreeningListService, ScreeningListServiceInterface}
import utils.BaseRouter

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class ScreeningRouter(screeningListService: ScreeningListServiceInterface)
                     (implicit val ec: ExecutionContext)
  extends BaseRouter {

  def routes: Route = pathPrefix(screeningsPath) {
    (get
      & path(JavaUUID)
      & pathEndOrSingleSlash) { screeningId =>
      onComplete(screeningListService.getScreeningDetails(screeningId)) {
        case Success(result) => result match {
          case ScreeningListService.Found(screeningWithSeats) => complete(screeningWithSeats)
          case ScreeningListService.NotFound                  => complete(NotFound -> "Screening not found")
        }
        case Failure(_) => complete(InternalServerError -> s"Internal server error")
      }
    }
  }
}

object ScreeningRouter {
  val screeningsPath = "screenings"
}
