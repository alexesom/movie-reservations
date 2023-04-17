package repositories

import cats.data.OptionT
import com.typesafe.scalalogging.LazyLogging
import domain.{Screening, Seat}
import repositories.tables.ReservationsTable.reservationsTableQuery
import repositories.tables.ReservedSeatsTable.reservedSeatsTableQuery
import repositories.tables.ScreeningsTable
import repositories.tables.SeatsTable.seatsTableQuery
import slick.jdbc.PostgresProfile.api._

import java.time.LocalDateTime
import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

trait ScreeningRepositoryInterface extends LazyLogging {
  def getScreening(screeningId: UUID): OptionT[Future, Screening]

  def getAvailableSeats(screeningId: UUID): OptionT[Future, Seq[Seat]]
}

class ScreeningRepository(implicit ec: ExecutionContext, db: Database)
  extends ScreeningRepositoryInterface {

  override def getScreening(screeningId: UUID): OptionT[Future, Screening] = {
    val screeningQuery = ScreeningsTable.screeningsTableQuery
    val query          = screeningQuery.filter(_.id === screeningId).result.headOption

    OptionT {
      db.run(query)
    }
  }

  override def getAvailableSeats(screeningId: UUID): OptionT[Future, Seq[Seat]] = {
    getScreening(screeningId).flatMap { screening =>
      val availableSeatsQuery = filterAvailableSeats(screening)

      OptionT.liftF {
        db.run(availableSeatsQuery)
      }
    }
  }

  private def filterAvailableSeats(screening: Screening) = {
    val reservedSeatsIds = getReservedSeatsIds(screening)

    seatsTableQuery
      .filter(_.roomId === screening.roomId)
      .filterNot(_.id in reservedSeatsIds)
      .sortBy(seat => (seat.rowNumber, seat.seatNumber))
      .result
  }

  private def getReservedSeatsIds(screening: Screening) = {
    reservedSeatsTableQuery
      .join(reservationsTableQuery)
      .on(_.reservationId === _.id)
      .filter(_._2.screeningId === screening.id)
      .filter(_._2.expirationTime > LocalDateTime.now())
      .filter(_._1.expired === false)
      .map(_._1.seatId)
  }
}