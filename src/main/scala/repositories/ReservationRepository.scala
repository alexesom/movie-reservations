package repositories

import cats.data.OptionT
import com.typesafe.scalalogging.LazyLogging
import domain.{PaymentStatus, Reservation, ReservedSeat, Seat}
import repositories.tables.ReservationsTable.reservationsTableQuery
import repositories.tables.ReservedSeatsTable.reservedSeatsTableQuery
import slick.jdbc.PostgresProfile.api._

import java.time.LocalDateTime
import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

trait ReservationRepositoryInterface extends LazyLogging {
  def validateSeatSelection(selectedSeatsIds: Seq[UUID], screeningId: UUID): OptionT[Future, Boolean]

  def createReservation(reservation: Reservation, reservedSeats: Seq[ReservedSeat]): Future[UUID]

  def getReservation(reservationId: UUID): OptionT[Future, Reservation]

  def getReservedSeats(reservationId: UUID): OptionT[Future, Seq[ReservedSeat]]

  def cancelExpiredReservations(): Future[Unit]
}

class ReservationRepository(
                             screeningRepository: ScreeningRepositoryInterface,
                             seatsRepository: SeatRepositoryInterface
                           )
                           (implicit val ec: ExecutionContext, db: Database)
  extends ReservationRepositoryInterface {

  private def getReservedSeats(
                                screeningId: UUID,
                                allSeatsOpt: OptionT[Future, Seq[Seat]]
                              ): OptionT[Future, Seq[Seat]] = {
    val availableSeatsOpt = screeningRepository.getAvailableSeats(screeningId)

    for {
      availableSeats <- availableSeatsOpt
      allSeats       <- allSeatsOpt
    } yield allSeats.diff(availableSeats)
  }

  private def getSelectedSeats(
                                selectedSeatsIds: Seq[UUID],
                                allSeatsOpt: OptionT[Future, Seq[Seat]]
                              ): OptionT[Future, Seq[Seat]] = {
    for {
      allSeats <- allSeatsOpt
    } yield allSeats.filter(seat => selectedSeatsIds.contains(seat.id))
  }

  def validateSeatSelection(selectedSeatsIds: Seq[UUID], screeningId: UUID): OptionT[Future, Boolean] = {
    val allSeatsOpt      = seatsRepository.getSeatsByScreeningId(screeningId)

    val reservedSeatsOpt = getReservedSeats(screeningId, allSeatsOpt)

    val selectedSeatsOpt = getSelectedSeats(selectedSeatsIds, allSeatsOpt)

    val selectedAndReservedSeatsOpt = for {
      selectedSeats <- selectedSeatsOpt
      reservedSeats <- reservedSeatsOpt
    } yield selectedSeats ++ reservedSeats

    for {
      allSeats <- allSeatsOpt
      selectedSeats <- selectedSeatsOpt
      reservedSeats <- reservedSeatsOpt
      selectedAndReservedSeats <- selectedAndReservedSeatsOpt
    } yield {
      val allSeatsSelectedAreAvailable = selectedSeats.forall(seat => !reservedSeats.exists(_.id == seat.id))
      val noSingleEmptySeatsInRow = checkNoSingleEmptySeatsInRow(allSeats, selectedAndReservedSeats)

      allSeatsSelectedAreAvailable && noSingleEmptySeatsInRow
    }
  }

  private def checkNoSingleEmptySeatsInRow(allSeats: Seq[Seat], selectedAndReservedSeats: Seq[Seat]): Boolean = {
    val seatsByRow = allSeats.groupBy(_.rowNumber)

    seatsByRow.forall {
      case (_, seatsInRow) =>
        val selectedSeatsInRow = selectedAndReservedSeats.filter(seat => seatsInRow.exists(_.id == seat.id))

        val selectedSeatNumbersInRow = selectedSeatsInRow.map(_.seatNumber).sorted


        selectedSeatNumbersInRow.sliding(2).forall {
          case Seq(prev, next) => next - prev != 2
        }
    }
  }

  override def createReservation(reservation: Reservation, reservedSeats: Seq[ReservedSeat]): Future[UUID] = {
    val reservationInsert = reservationsTableQuery += reservation
    val reservedSeatsInsert = reservedSeatsTableQuery ++= reservedSeats

    val query = (for {
      _ <- reservationInsert
      _ <- reservedSeatsInsert
    } yield ()).transactionally

    db.run(query).map(_ => reservation.id)
  }

  override def getReservation(reservationId: UUID): OptionT[Future, Reservation] = {
    val query = reservationsTableQuery.filter(_.id === reservationId)

    OptionT(db.run(query.result.headOption))
  }

  override def getReservedSeats(reservationId: UUID): OptionT[Future, Seq[ReservedSeat]] = {
    val query = reservedSeatsTableQuery.filter(_.reservationId === reservationId)

    OptionT(db.run(query.result).map(_.toSeq).map(Option.apply))
  }

  override def cancelExpiredReservations(): Future[Unit] = {
    val now = LocalDateTime.now()

    val expiredReservationsQuery   = reservationsTableQuery.filter(_.expirationTime < now)

    val expiredReservationIdsQuery = expiredReservationsQuery.map(_.id)

    val expiredReservedSeatsQuery  = reservedSeatsTableQuery.filter(_.reservationId in expiredReservationIdsQuery)

    val updateExpiredSeatsAction   = expiredReservedSeatsQuery.map(_.expired).update(true)

    val updateExpiredReservationsAction = expiredReservationsQuery.map(_.paymentStatus).update(PaymentStatus.Expired)

    db.run(DBIO.seq(updateExpiredSeatsAction, updateExpiredReservationsAction).transactionally)
  }
}
