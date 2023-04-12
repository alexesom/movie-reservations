package repositories.tables

import domain.ReservedSeat
import repositories.tables.ReservationsTable.reservationsTableQuery
import repositories.tables.SeatsTable.seatsTableQuery
import slick.jdbc.PostgresProfile.api._

import java.util.UUID



class ReservedSeatsTable(tag: Tag) extends Table[ReservedSeat](tag, "reserved_seats") {
  def id            = column[UUID]("id", O.PrimaryKey)
  def reservationId = column[UUID]("reservation_id")
  def seatId        = column[UUID]("seat_id")
  def ticketType    = column[String]("ticket_type")
  def expired       = column[Boolean]("expired")

  def * = (id, reservationId, seatId, ticketType, expired) <> (ReservedSeat.tupled, ReservedSeat.unapply)

  def reservation = foreignKey("fk_reserved_seats_reservations", reservationId, reservationsTableQuery)(_.id)
  def seat = foreignKey("fk_reserved_seats_seats", seatId, seatsTableQuery)(_.id)
}

object ReservedSeatsTable {
  lazy val reservedSeatsTableQuery = TableQuery[ReservedSeatsTable]
}
