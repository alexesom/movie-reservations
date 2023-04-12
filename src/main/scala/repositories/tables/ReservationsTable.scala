package repositories.tables

import domain.Reservation
import slick.jdbc.PostgresProfile.api._
import ScreeningsTable.screeningsTableQuery
import domain.PaymentStatus._

import java.time.LocalDateTime
import java.util.UUID

class ReservationsTable(tag: Tag) extends Table[Reservation](tag, "reservations") {
  def id              = column[UUID]("id", O.PrimaryKey)
  def screeningId     = column[UUID]("screening_id")
  def firstName       = column[String]("first_name")
  def lastName        = column[String]("last_name")
  def reservationTime = column[LocalDateTime]("reservation_time")
  def expirationTime  = column[LocalDateTime]("expiration_time")
  def paymentStatus   = column[PaymentStatus]("payment_status")

  def * = (id, screeningId, firstName, lastName, reservationTime, expirationTime, paymentStatus) <> (Reservation.tupled, Reservation.unapply)

  def screening = foreignKey("fk_reservations_screenings", screeningId, screeningsTableQuery)(_.id)
}

object ReservationsTable {
  lazy val reservationsTableQuery = TableQuery[ReservationsTable]
}
