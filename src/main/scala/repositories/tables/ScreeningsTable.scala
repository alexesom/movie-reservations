package repositories.tables

import MoviesTable.movieTableQuery
import domain.Screening
import slick.jdbc.PostgresProfile.api._
import RoomsTable.roomsTableQuery

import java.time.LocalDateTime
import java.util.UUID

class ScreeningsTable(tag: Tag) extends Table[Screening](tag, "screenings") {
  def id        = column[UUID]("id", O.PrimaryKey)
  def movieId   = column[UUID]("movie_id")
  def roomId    = column[UUID]("room_id")
  def startTime = column[LocalDateTime]("start_time")

  def * = (id, movieId, roomId, startTime) <> (Screening.tupled, Screening.unapply)

  def movie = foreignKey("fk_screenings_movies", movieId, movieTableQuery)(_.id)
  def room = foreignKey("fk_screenings_rooms", roomId, roomsTableQuery)(_.id)
}

object ScreeningsTable {
  lazy val screeningsTableQuery = TableQuery[ScreeningsTable]
}
