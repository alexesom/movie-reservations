package domain

import io.circe.generic.extras.semiauto.{deriveConfiguredDecoder, deriveConfiguredEncoder}
import io.circe.{Decoder, Encoder}
import utils.CirceImplicits

import java.util.UUID

final case class ReservedSeat(
                               id:            UUID,
                               reservationId: UUID,
                               seatId:        UUID,
                               ticketType:    String,
                               expired:       Boolean
                             )

object ReservedSeat extends CirceImplicits {
  def tupled = (ReservedSeat.apply _).tupled

  implicit val reservationSeatEncoder: Encoder[ReservedSeat] = deriveConfiguredEncoder
  implicit val reservationSeatDecoder: Decoder[ReservedSeat] = deriveConfiguredDecoder
}