package modules

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Route
import com.softwaremill.macwire.wire
import com.typesafe.config.Config
import repositories.ScreeningRepository
import routers.ScreeningRouter
import services.ScreeningListService
import slick.jdbc.PostgresProfile.api._
import utils.RouteProvider

import scala.concurrent.ExecutionContext

trait ScreeningModuleDependencies {
  implicit val executor: ExecutionContext
  implicit val db: Database
  val config: Config
  val system: ActorSystem
}

trait ScreeningModuleInterface {
  def screeningRepository: ScreeningRepository
}

trait ScreeningModule
  extends ScreeningModuleInterface
    with ScreeningModuleDependencies
    with RouteProvider {

  override lazy val screeningRepository               = wire[ScreeningRepository]

  lazy val screeningListService: ScreeningListService = wire[ScreeningListService]

  lazy val screeningRouter                            = wire[ScreeningRouter]

  abstract override def route: Route = super.route ~ screeningRouter.routes
}
