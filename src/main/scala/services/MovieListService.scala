package services

import com.typesafe.scalalogging.LazyLogging
import domain.dto.{MovieWithScreeningsInfo, ScreeningInfo}
import domain.{Movie, Screening}
import repositories.MovieRepository.MoviesWithScreenings
import repositories.MovieRepositoryInterface

import java.time.LocalDateTime
import scala.concurrent.{ExecutionContext, Future}

trait MovieListServiceInterface extends LazyLogging {
  def listMovieScreenings(fromTime: LocalDateTime, toTime: LocalDateTime): Future[Seq[MovieWithScreeningsInfo]]
}

class MovieListService(movieRepository: MovieRepositoryInterface)
                      (implicit val ec: ExecutionContext)
  extends MovieListServiceInterface {
  private def createScreeningInfo(screening: Screening, movieDuration: Int): ScreeningInfo = {
    val endTime = screening.startTime.plusMinutes(movieDuration)
    ScreeningInfo(screening.id, screening.startTime, endTime)
  }

  private def createMovieWithScreeningsInfo(movie: Movie, screenings: Seq[Screening]): MovieWithScreeningsInfo = {
    val screeningInfos = screenings
      .map(screening => createScreeningInfo(screening, movie.durationMinutes))
      .sortBy(_.screeningStartTime)
    MovieWithScreeningsInfo(movie, screeningInfos)
  }

  override def listMovieScreenings(fromTime: LocalDateTime, toTime: LocalDateTime): Future[Seq[MovieWithScreeningsInfo]] = {
    logger.info(s"Listing movies with screenings between $fromTime and $toTime")
    val movieScreenings: Future[MoviesWithScreenings] = movieRepository.listMovieScreenings(fromTime, toTime)

    movieScreenings.map { moviesWithScreenings =>
      moviesWithScreenings.map {
        case (movie, screenings) => createMovieWithScreeningsInfo(movie, screenings)
      }
        .toSeq
        .sortBy(_.movie.title)
    }
  }
}
