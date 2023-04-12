package domain.dto

import io.circe.{Decoder, Encoder}
import io.circe.generic.extras.semiauto.{deriveConfiguredDecoder, deriveConfiguredEncoder}
import utils.CirceImplicits

import java.time.LocalDateTime
import java.util.UUID

case class ScreeningInfo(
                          screeningId:        UUID,
                          screeningStartTime: LocalDateTime,
                          screeningEndTime:   LocalDateTime
                        )

object ScreeningInfo extends CirceImplicits {
  implicit val screeningTimeEncoder: Encoder[ScreeningInfo] = deriveConfiguredEncoder
  implicit val screeningTimeDecoder: Decoder[ScreeningInfo] = deriveConfiguredDecoder
}