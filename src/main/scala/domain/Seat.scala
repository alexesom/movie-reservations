package domain

import io.circe.{Decoder, Encoder}
import io.circe.generic.extras.semiauto.{deriveConfiguredDecoder, deriveConfiguredEncoder}
import utils.CirceImplicits


import java.util.UUID

case class Seat(
                 id:         UUID,
                 roomId:     UUID,
                 rowNumber:  Int,
                 seatNumber: Int
               )

object Seat extends CirceImplicits {
  def tupled = (Seat.apply _).tupled

  implicit val seatEncoder: Encoder[Seat] = deriveConfiguredEncoder
  implicit val seatDecoder: Decoder[Seat] = deriveConfiguredDecoder
}
