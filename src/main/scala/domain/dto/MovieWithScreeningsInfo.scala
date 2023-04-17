package domain.dto

import domain.Movie
import io.circe.generic.extras.semiauto.{deriveConfiguredDecoder, deriveConfiguredEncoder}
import io.circe.{Decoder, Encoder}
import utils.CirceImplicits

case class MovieWithScreeningsInfo(
                                    movie:          Movie,
                                    screeningTimes: Seq[ScreeningInfo]
                                  )

object MovieWithScreeningsInfo extends CirceImplicits {
  implicit val movieAndScreeningTimesEncoder: Encoder[MovieWithScreeningsInfo] = deriveConfiguredEncoder
  implicit val movieAndScreeningTimesDecoder: Decoder[MovieWithScreeningsInfo] = deriveConfiguredDecoder
}
