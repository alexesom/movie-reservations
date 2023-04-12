package modules

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Route
import com.softwaremill.macwire.wire
import com.typesafe.config.Config
import repositories.{ReservationRepository, ReservationRepositoryInterface, SeatRepository, SeatRepositoryInterface}
import routers.ReservationRouter
import services.{ReservationCreateService, ReservationListService}
import slick.jdbc.PostgresProfile.api._
import utils.RouteProvider

import scala.concurrent.ExecutionContext

trait ReservationModuleDependencies {
  implicit val executor: ExecutionContext
  implicit val db: Database
  val config: Config
  val system: ActorSystem
}

trait ReservationModuleInterface extends ReservationModuleDependencies with ScreeningModuleInterface {
  def reservationRepository: ReservationRepositoryInterface
  def seatRepository: SeatRepositoryInterface
}

trait ReservationModule
  extends ReservationModuleInterface
    with ReservationModuleDependencies
    with RouteProvider {

  override lazy val reservationRepository = wire[ReservationRepository]

  override lazy val seatRepository  = wire[SeatRepository]

  lazy val reservationCreateService = wire[ReservationCreateService]

  lazy val reservationListService   = wire[ReservationListService]

  lazy val reservationRouter        = wire[ReservationRouter]

  abstract override def route: Route = super.route ~ reservationRouter.routes
}
