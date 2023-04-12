package repositories.tables

import domain.Movie
import slick.jdbc.PostgresProfile.api._
import slick.lifted.Tag

import java.util.UUID

class MoviesTable(tag: Tag) extends Table[Movie](tag, "movies") {
  def id              = column[UUID]("id", O.PrimaryKey)
  def title           = column[String]("title")
  def durationMinutes = column[Int]("duration_minutes")
  def * = (id, title, durationMinutes) <> (Movie.tupled, Movie.unapply)
}

object MoviesTable {
  lazy val movieTableQuery = TableQuery[MoviesTable]
}