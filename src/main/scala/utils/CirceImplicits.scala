package utils

import domain.PaymentStatus
import domain.PaymentStatus.PaymentStatus
import io.circe.generic.extras.Configuration
import io.circe.{Decoder, Encoder, Json}

import java.time.LocalDateTime
import java.time.format.{DateTimeFormatter, DateTimeParseException}
import java.util.UUID

trait CirceImplicits {

  implicit val customConfig: Configuration = Configuration.default.withSnakeCaseMemberNames

  implicit val uuidEncoder: Encoder[UUID] = Encoder.instance(uuid => Json.fromString(uuid.toString))
  implicit val localDateTimeEncoder: Encoder[LocalDateTime] = Encoder.instance { dateTime =>
    val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    Json.fromString(dateTime.format(formatter))
  }

  implicit val uuidDecoder: Decoder[UUID] = Decoder.decodeString.emap { str =>
    try {
      Right(UUID.fromString(str))
    } catch {
      case _: IllegalArgumentException => Left(s"Invalid UUID format: $str")
    }
  }
  implicit val localDateTimeDecoder: Decoder[LocalDateTime] = Decoder.decodeString.emap { str=>
    val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    try {
      Right(LocalDateTime.parse(str, formatter))
    } catch {
      case _: DateTimeParseException => Left(s"Invalid DateTime format: $str")
    }
  }

  implicit val paymentStatusEncoder: Encoder[PaymentStatus] = Encoder.encodeString.contramap[PaymentStatus](_.toString)

  implicit val paymentStatusDecoder: Decoder[PaymentStatus] = Decoder.decodeString.map[PaymentStatus](PaymentStatus.withName)
}
