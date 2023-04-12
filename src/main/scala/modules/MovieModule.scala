package modules

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Route
import com.softwaremill.macwire.wire
import com.typesafe.config.Config
import repositories.MovieRepository
import routers.MovieRouter
import services.MovieListService
import slick.jdbc.PostgresProfile.api._
import utils.RouteProvider

import scala.concurrent.ExecutionContext

trait MovieModuleDependencies {
  implicit val executor: ExecutionContext
  implicit val db: Database
  val config: Config
  val system: ActorSystem
}

trait MovieModuleInterface {
  def movieRepository: MovieRepository
}

trait MovieModule
  extends MovieModuleInterface
    with MovieModuleDependencies
    with RouteProvider {

  override lazy val movieRepository           = wire[MovieRepository]

  lazy val movieListService: MovieListService = wire[MovieListService]

  lazy val movieRouter                        = wire[MovieRouter]

  abstract override def route: Route = super.route ~ movieRouter.routes
}
