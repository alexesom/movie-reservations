package domain

import io.circe.{Decoder, Encoder}
import io.circe.generic.extras.semiauto.{deriveConfiguredDecoder, deriveConfiguredEncoder}
import utils.CirceImplicits

import java.util.UUID

final case class Room(
                       id:         UUID,
                       name:       String,
                       totalSeats: Int
                     )

object Room extends CirceImplicits {
  def tupled = (Room.apply _).tupled

  implicit val roomEncoder: Encoder[Room] = deriveConfiguredEncoder
  implicit val roomDecoder: Decoder[Room] = deriveConfiguredDecoder
}
