package utils

import akka.actor.{Actor, ActorLogging}
import repositories.ReservationRepository

class ExpiredReservationChecker(reservationRepository: ReservationRepository) extends Actor with ActorLogging {
  override def receive: Receive = {
    case "checkExpiredReservations" =>
      log.info("Checking for expired reservations...")

      reservationRepository.cancelExpiredReservations()
  }
}
