package repositories

import cats.data.OptionT
import domain.Seat
import repositories.tables.ScreeningsTable.screeningsTableQuery
import repositories.tables.SeatsTable.seatsTableQuery
import slick.jdbc.PostgresProfile.api._

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

trait SeatRepositoryInterface {
  def getSeatsByScreeningId(screeningId: UUID): OptionT[Future, Seq[Seat]]
}
class SeatRepository(implicit val ec: ExecutionContext, db: Database)
  extends SeatRepositoryInterface {

  def getSeatsByScreeningId(screeningId: UUID): OptionT[Future, Seq[Seat]] = {
    val seatsQuery = for {
      screening <- screeningsTableQuery if screening.id === screeningId
      seats     <- seatsTableQuery      if seats.roomId === screening.roomId
    } yield seats

    OptionT.liftF {
      db.run(seatsQuery.result)
    }
  }
}
