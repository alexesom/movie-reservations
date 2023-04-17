package domain


import domain.PaymentStatus.PaymentStatus
import io.circe.{Decoder, Encoder}
import io.circe.generic.extras.semiauto.{deriveConfiguredDecoder, deriveConfiguredEncoder}
import utils.CirceImplicits

import java.time.LocalDateTime
import java.util.UUID

final case class Reservation(
                        id:              UUID,
                        screeningId:     UUID,
                        firstName:       String,
                        lastName:        String,
                        reservationTime: LocalDateTime,
                        expirationTime:  LocalDateTime,
                        paymentStatus:   PaymentStatus
                      )

object Reservation extends CirceImplicits {
  def tupled = (Reservation.apply _).tupled

  implicit val reservationEncoder: Encoder[Reservation] = deriveConfiguredEncoder
  implicit val reservationDecoder: Decoder[Reservation] = deriveConfiguredDecoder
}