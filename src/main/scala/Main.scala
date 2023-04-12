import akka.actor.Props
import akka.http.scaladsl.Http
import utils.{ExpiredReservationChecker, Handlers}

import scala.concurrent.duration.DurationInt
import scala.io.StdIn

object Main extends App with ApplicationLoader with Handlers {

  val server = Http().newServerAt(config.getString("http.interface"), config.getInt("http.port")).bind(routes)
  System.out.println(s"Server online at http://${config.getString("http.interface")}:${config.getInt("http.port")}/")

  private val expiredReservationChecker = system.actorOf(
    Props(classOf[ExpiredReservationChecker], reservationRepository),
    "expiredReservationChecker"
  )

  system.scheduler.scheduleAtFixedRate(
    initialDelay = 1.minute,
    interval = 1.minute,
    receiver = expiredReservationChecker,
    message = "checkExpiredReservations"
  )

  StdIn.readLine()
  server
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}