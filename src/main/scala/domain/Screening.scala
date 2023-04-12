package domain

import io.circe.generic.extras.semiauto.{deriveConfiguredDecoder, deriveConfiguredEncoder}
import io.circe.{Decoder, Encoder}
import utils.CirceImplicits

import java.time.LocalDateTime
import java.util.UUID

final case class Screening(
                            id:        UUID,
                            movieId:   UUID,
                            roomId:    UUID,
                            startTime: LocalDateTime
                          )

object Screening extends CirceImplicits {
  def tupled = (Screening.apply _).tupled

  implicit val screeningEncoder: Encoder[Screening] = deriveConfiguredEncoder
  implicit val screeningDecoder: Decoder[Screening] = deriveConfiguredDecoder
}
