package services

import cats.data.OptionT
import com.typesafe.scalalogging.LazyLogging
import domain.dto.{CreateReservationRequest, ReservationInfo}
import domain.{PaymentStatus, Reservation, ReservedSeat}
import repositories.{ReservationRepositoryInterface, ScreeningRepositoryInterface}
import services.ReservationCreateService._

import java.time.LocalDateTime
import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

trait ReservationCreateServiceInterface extends LazyLogging {
  def createReservation(reservationRequest: CreateReservationRequest): Future[ReservationCreateResult]
}

class ReservationCreateService(
                                reservationRepository: ReservationRepositoryInterface,
                                screeningRepository: ScreeningRepositoryInterface
                              )
                              (implicit val ec: ExecutionContext)
  extends ReservationCreateServiceInterface {

  private def isValidName(value: String): Boolean = {
    val pattern = "^[A-Z][a-ząćęłńóśźżA-ZĄĆĘŁŃÓŚŹŻ]{2,}$".r
    pattern.matches(value)
  }

  private def isValidSurname(value: String): Boolean = {
    val pattern = "^[A-Z][a-ząćęłńóśźżA-ZĄĆĘŁŃÓŚŹŻ]{2,}(-[A-Z][a-ząćęłńóśźżA-ZĄĆĘŁŃÓŚŹŻ]{2,})?$".r
    pattern.matches(value)
  }

  override def createReservation(reservationRequest: CreateReservationRequest): Future[ReservationCreateResult] = {
    logger.info(s"Creating reservation for: ${reservationRequest.firstName} ${reservationRequest.lastName}")
    validateReservationRequest(reservationRequest) match {
      case Some(error) => Future.successful(error)
      case None        => processReservation(reservationRequest)
    }
  }

  private def validateReservationRequest(reservationRequest: CreateReservationRequest): Option[ReservationCreateResult] = {
    if (!isValidName(reservationRequest.firstName) || !isValidSurname(reservationRequest.lastName))
      Some(InvalidFirstNameOrLastNameFormat)
    else if (reservationRequest.selectedSeatsWithTicketsTypes.isEmpty)
      Some(NoSeatsSelected)
    else
      None
  }

  private def processReservation(reservationRequest: CreateReservationRequest): Future[ReservationCreateResult] = {
    reservationRepository
      .validateSeatSelection(
        reservationRequest.selectedSeatsWithTicketsTypes.map(_.seatId),
        reservationRequest.screeningId
      )
      .getOrElse(false)
      .flatMap { isValid =>
        if (isValid) createAndSaveReservation(reservationRequest)
        else         Future.successful(InvalidSeatSelection)
      }
  }

  private def createAndSaveReservation(reservationRequest: CreateReservationRequest): Future[ReservationCreateResult] = {
    val isReservationPossibleF = checkIfReservationIsPossibleByTime(reservationRequest.screeningId).getOrElse(false)

    isReservationPossibleF.flatMap { isReservationPossible =>
      if (isReservationPossible) {
        val reservation = createReservationObject(reservationRequest)
        val reservedSeats = createReservedSeatsObjects(reservationRequest, reservation.id)
        val reservationInfo = ReservationListService.createReservationInfo(reservation, reservedSeats)

        for {
          _ <- reservationRepository.createReservation(reservation, reservedSeats)
        } yield Created(reservationInfo)
      } else
        Future.successful(ReservationNotPossible)
    }
  }

  private def checkIfReservationIsPossibleByTime(screeningId: UUID): OptionT[Future, Boolean] = {
    val currentTime    = LocalDateTime.now()
    val screening      = screeningRepository.getScreening(screeningId)
    val screeningTime  = screening.map(_.startTime)
    val timeDifference = screeningTime.map(_.minusMinutes(15))

    for {
      timeDifference <- timeDifference
    } yield {
      if(currentTime.isBefore(timeDifference)) {
        true
      } else {
        false
      }
    }
  }
  private def createReservationObject(reservationRequest: CreateReservationRequest): Reservation = {
    val reservationId  = UUID.randomUUID()
    val currentTime    = LocalDateTime.now()
    val expirationTime = currentTime.plusMinutes(15)

    Reservation(
      id              = reservationId,
      screeningId     = reservationRequest.screeningId,
      firstName       = reservationRequest.firstName,
      lastName        = reservationRequest.lastName,
      reservationTime = currentTime,
      expirationTime  = expirationTime,
      paymentStatus   = PaymentStatus.Pending
    )
  }

  private def createReservedSeatsObjects(reservationRequest: CreateReservationRequest, reservationId: UUID): Seq[ReservedSeat] = {
    reservationRequest.selectedSeatsWithTicketsTypes.map { seatInfoWithTicketType =>
      val reservedSeatId = UUID.randomUUID()

      ReservedSeat(
        id            = reservedSeatId,
        reservationId = reservationId,
        seatId        = seatInfoWithTicketType.seatId,
        ticketType    = seatInfoWithTicketType.ticketType,
        expired       = false
      )
    }
  }
}

object ReservationCreateService {

  sealed trait ReservationCreateResult extends Product with Serializable

  case class Created(reservationInfo: ReservationInfo) extends ReservationCreateResult

  case object InvalidFirstNameOrLastNameFormat extends ReservationCreateResult

  case object NoSeatsSelected extends ReservationCreateResult

  case object InvalidSeatSelection extends ReservationCreateResult

  case object ReservationNotPossible extends ReservationCreateResult
}
