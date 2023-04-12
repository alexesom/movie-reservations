package routers

import akka.http.scaladsl.model.StatusCodes.{BadRequest, Created, InternalServerError}
import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import domain.dto.CreateReservationRequest
import io.circe.generic.codec.DerivedAsObjectCodec.deriveCodec
import routers.ReservationRouter.reservationsPath
import services.{ReservationCreateService, ReservationCreateServiceInterface, ReservationListService, ReservationListServiceInterface}
import utils.BaseRouter

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class ReservationRouter(
                         reservationCreateService: ReservationCreateServiceInterface,
                         reservationListService: ReservationListServiceInterface
                       )(implicit val ec: ExecutionContext)
  extends BaseRouter {

  def routes: Route = pathPrefix(reservationsPath) {
    path(JavaUUID) { reservationId =>
      (get
        & pathEndOrSingleSlash) {
        onComplete(reservationListService.getReservationInfo(reservationId)) {
          case Success(result) => result match {
            case ReservationListService.Found(reservationInfo) => complete(reservationInfo)
            case ReservationListService.NotFound               => complete(BadRequest -> "Reservation not found")
          }
          case Failure(_) => complete(InternalServerError -> s"Internal server error")
        }
      }
    } ~
      (post
        & pathEndOrSingleSlash
        & entity(as[CreateReservationRequest])) { request =>
        onComplete(reservationCreateService.createReservation(request)) {
          case Success(result) => result match {
            case ReservationCreateService.Created(reservationId)           => complete(Created -> reservationId)
            case ReservationCreateService.InvalidFirstNameOrLastNameFormat => complete(BadRequest -> "Invalid first name or last name format")
            case ReservationCreateService.NoSeatsSelected                  => complete(BadRequest -> "No seats selected")
            case ReservationCreateService.InvalidSeatSelection             => complete(BadRequest -> "Invalid seat selection")
            case ReservationCreateService.ReservationNotPossible           => complete(BadRequest -> "Reservation not possible")
          }
          case Failure(_) => complete(InternalServerError -> s"Internal server error")
        }
      }
  }
}

object ReservationRouter {
  val reservationsPath = "reservations"
}
