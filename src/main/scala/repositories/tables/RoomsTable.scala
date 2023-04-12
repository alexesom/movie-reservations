package repositories.tables

import domain.Room
import slick.jdbc.PostgresProfile.api._

import java.util.UUID

class RoomsTable(tag: Tag) extends Table[Room](tag, "rooms") {
  def id         = column[UUID]("id", O.PrimaryKey)
  def name       = column[String]("name")
  def totalSeats = column[Int]("total_seats")

  def * = (id, name, totalSeats) <> (Room.tupled, Room.unapply)
}

object RoomsTable {
  lazy val roomsTableQuery = TableQuery[RoomsTable]
}