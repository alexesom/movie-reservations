package domain.dto

import io.circe.generic.semiauto.deriveCodec

import java.util.UUID

final case class SeatInfoWithTicketType(
                                         seatId:     UUID,
                                         ticketType: String
                                       )

object SeatInfoWithTicketType {
  implicit val seatInfoWithTicketTypeCodec = deriveCodec[SeatInfoWithTicketType]
}