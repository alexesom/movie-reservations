package domain.dto

import domain.PaymentStatus.PaymentStatus

import java.time.LocalDateTime
import java.util.UUID

case class ReservationInfo(
                            reservationId:    UUID,
                            firstName:        String,
                            lastName:         String,
                            screeningId:      UUID,
                            reservedSeatsIds: Seq[UUID],
                            expirationTime:   LocalDateTime,
                            expired:          Boolean,
                            paymentStatus:    PaymentStatus,
                            amountToPay:      BigDecimal
                          )
