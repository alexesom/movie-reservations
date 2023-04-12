package services

import com.typesafe.scalalogging.LazyLogging
import domain.dto.ReservationInfo
import domain.{Reservation, ReservedSeat}
import repositories.ReservationRepository
import services.ReservationListService.{Found, NotFound, ReservationListResult}
import utils.TicketPaymentCalculator

import java.time.LocalDateTime
import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

trait ReservationListServiceInterface extends LazyLogging {
  def getReservationInfo(reservationId: UUID): Future[ReservationListResult]
}

class ReservationListService(reservationRepository: ReservationRepository)
                            (implicit val ec: ExecutionContext)
  extends ReservationListServiceInterface {
  override def getReservationInfo(reservationId: UUID): Future[ReservationListResult] = {
    logger.info(s"Getting reservation info for reservation with id: $reservationId")
    val reservationOpt   = reservationRepository.getReservation(reservationId)
    val reservedSeatsOpt = reservationRepository.getReservedSeats(reservationId)

    (for {
      reservation   <- reservationOpt
      reservedSeats <- reservedSeatsOpt
    } yield {
      val reservationInfo = ReservationListService.createReservationInfo(reservation, reservedSeats)

      Found(reservationInfo)
    })
      .getOrElse(NotFound)
  }
}

object ReservationListService {
  def createReservationInfo(reservation: Reservation, reservedSeats: Seq[ReservedSeat]): ReservationInfo = {
    ReservationInfo(
      reservationId    = reservation.id,
      firstName        = reservation.firstName,
      lastName         = reservation.lastName,
      screeningId      = reservation.screeningId,
      reservedSeatsIds = reservedSeats.map(_.seatId),
      expirationTime   = reservation.expirationTime,
      expired          = reservation.expirationTime.isBefore(LocalDateTime.now()),
      paymentStatus    = reservation.paymentStatus,
      amountToPay      = TicketPaymentCalculator.calculateAmountToPay(reservedSeats)
    )
  }

  sealed trait ReservationListResult extends Product with Serializable

  case class Found(reservationInfo: ReservationInfo) extends ReservationListResult

  case object NotFound extends ReservationListResult
}
