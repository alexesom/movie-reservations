package repositories.tables

import RoomsTable.roomsTableQuery
import domain.Seat
import slick.jdbc.PostgresProfile.api._

import java.util.UUID

class SeatsTable(tag: Tag) extends Table[Seat](tag, "seats") {
  def id         = column[UUID]("id", O.PrimaryKey)
  def roomId     = column[UUID]("room_id")
  def rowNumber  = column[Int]("row_number")
  def seatNumber = column[Int]("seat_number")

  def * = (id, roomId, rowNumber, seatNumber) <> (Seat.tupled, Seat.unapply)

  def room = foreignKey("fk_seats_rooms", roomId, roomsTableQuery)(_.id)
}

object SeatsTable {
  lazy val seatsTableQuery = TableQuery[SeatsTable]
}
