package domain

import slick.ast.BaseTypedType
import slick.jdbc.JdbcType
import slick.jdbc.PostgresProfile.api._

object PaymentStatus extends Enumeration {
  type PaymentStatus = Value
  val Pending, Paid, Expired, Canceled = Value


  implicit val paymentStatusColumnType: JdbcType[PaymentStatus] with BaseTypedType[PaymentStatus] =
    MappedColumnType.base[PaymentStatus, String](
      paymentStatus => paymentStatus.toString,
      string        => PaymentStatus.withName(string)
    )
}
