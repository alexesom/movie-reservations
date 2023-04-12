package domain.dto

import domain.{Screening, Seat}
import io.circe.generic.extras.semiauto.{deriveConfiguredDecoder, deriveConfiguredEncoder}
import io.circe.{Decoder, Encoder}
import utils.CirceImplicits

case class ScreeningWithSeats(
                               screening: Screening,
                               seats:     Seq[Seat]
                             )

object ScreeningWithSeats extends CirceImplicits {
  implicit val screeningWithSeatsEncoder: Encoder[ScreeningWithSeats] = deriveConfiguredEncoder
  implicit val screeningWithSeatsDecoder: Decoder[ScreeningWithSeats] = deriveConfiguredDecoder
}
