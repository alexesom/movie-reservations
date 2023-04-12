import akka.actor.ActorSystem
import akka.http.scaladsl.server.Route
import com.typesafe.config.{Config, ConfigFactory}
import modules.{MovieModule, ReservationModule, ScreeningModule}
import slick.jdbc.PostgresProfile.api._
import utils.RouteProvider

import scala.concurrent.ExecutionContextExecutor

trait ApplicationLoader
  extends RouteProvider
  with MovieModule
  with ScreeningModule
  with ReservationModule {
    lazy implicit val system: ActorSystem = ActorSystem()
    lazy implicit val executor: ExecutionContextExecutor = system.dispatcher

    lazy val db: Database      = Database.forConfig("db", config)

    lazy val config: Config    = ConfigFactory.load()

    lazy val apiPrefix         = config.getString("app.apiPrefix")
    lazy val apiVersion        = config.getString("app.apiVersion")
    lazy val pathPrefixMatcher = Slash ~ apiPrefix / apiVersion

    lazy val routes: Route = rawPathPrefix(pathPrefixMatcher) {
      route
    }
}
