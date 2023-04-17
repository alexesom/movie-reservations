package domain.dto


import io.circe.generic.semiauto.deriveCodec

import java.util.UUID

final case class CreateReservationRequest(
                                           screeningId:                   UUID,
                                           firstName:                     String,
                                           lastName:                      String,
                                           selectedSeatsWithTicketsTypes: Seq[SeatInfoWithTicketType]
                                         )

object CreateReservationRequest {
  implicit val createReservationRequestCodec = deriveCodec[CreateReservationRequest]
}