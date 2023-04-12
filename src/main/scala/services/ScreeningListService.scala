package services

import com.typesafe.scalalogging.LazyLogging
import domain.dto.ScreeningWithSeats
import repositories.ScreeningRepositoryInterface
import services.ScreeningListService.{Found, NotFound, ScreeningListResult}

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

trait ScreeningListServiceInterface extends LazyLogging {
  def getScreeningDetails(screeningId: UUID): Future[ScreeningListResult]
}

class ScreeningListService(screeningRepository: ScreeningRepositoryInterface)
                          (implicit val ec: ExecutionContext)
    extends ScreeningListServiceInterface {

    def getScreeningDetails(screeningId: UUID): Future[ScreeningListResult] = {
      logger.info(s"Getting screening details for screening $screeningId")

      (for {
        screening      <- screeningRepository.getScreening(screeningId)
        availableSeats <- screeningRepository.getAvailableSeats(screening.id)
      } yield {
        val screeningWithSeats = ScreeningWithSeats(screening, availableSeats)

        Found(screeningWithSeats)
      })
        .getOrElse(NotFound)
    }
}

object ScreeningListService {

  sealed trait ScreeningListResult extends Product with Serializable

  case class Found(screeningWithSeats: ScreeningWithSeats) extends ScreeningListResult

  case object NotFound extends ScreeningListResult
}