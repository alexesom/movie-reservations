package domain

import java.util.UUID
import io.circe.{Decoder, Encoder}
import io.circe.generic.extras.semiauto.{deriveConfiguredDecoder, deriveConfiguredEncoder}
import utils.CirceImplicits

final case class Movie(
                        id:              UUID,
                        title:           String,
                        durationMinutes: Int
                      )

object Movie extends CirceImplicits {
  def tupled = (Movie.apply _).tupled

  implicit val movieEncoder: Encoder[Movie] = deriveConfiguredEncoder
  implicit val movieDecoder: Decoder[Movie] = deriveConfiguredDecoder
}