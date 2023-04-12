package utils

import domain.ReservedSeat

object TicketPaymentCalculator {
  def calculateAmountToPay(reservedSeats: Seq[ReservedSeat]): BigDecimal = {
    reservedSeats.map { reservedSeat =>
      reservedSeat.ticketType match {
        case "adult"   => BigDecimal(25)
        case "student" => BigDecimal(18)
        case "child"   => BigDecimal(12.5)
      }
    }.sum
  }
}
