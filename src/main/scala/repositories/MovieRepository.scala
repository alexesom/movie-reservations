package repositories

import com.typesafe.scalalogging.LazyLogging
import domain.{Movie, Screening}
import repositories.MovieRepository.{MovieScreeningPair, MoviesWithScreenings}
import repositories.tables.{MoviesTable, ScreeningsTable}
import slick.jdbc.PostgresProfile.api._

import java.time.LocalDateTime
import scala.concurrent.{ExecutionContext, Future}

trait MovieRepositoryInterface extends LazyLogging {
  def listMovieScreenings(fromTime: LocalDateTime, toTime: LocalDateTime): Future[MoviesWithScreenings]
}

class MovieRepository(implicit ec: ExecutionContext, db: Database)
  extends MovieRepositoryInterface {

  override def listMovieScreenings(fromTime: LocalDateTime, toTime: LocalDateTime): Future[MoviesWithScreenings] = {
    val movieQuery = MoviesTable.movieTableQuery
    val screeningQuery = ScreeningsTable.screeningsTableQuery

    db.run(movieQuery
      .join(screeningQuery)
      .on(_.id === _.movieId)
      .filter {
        case (_, screening) =>
          screening.startTime >= fromTime && screening.startTime <= toTime
      }
      .result
      .map(groupScreeningsByMovie))
  }

    private def groupScreeningsByMovie(screenings: Seq[MovieScreeningPair]): MoviesWithScreenings = {
      screenings
        .groupBy(_._1)
        .view
        .mapValues(x => x.map(_._2))
        .toMap
    }
}

object MovieRepository {
  type MovieScreeningPair = (Movie, Screening)
  type MoviesWithScreenings = Map[Movie, Seq[Screening]]
}