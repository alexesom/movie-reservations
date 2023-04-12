package utils

import akka.http.scaladsl.server.{Directives, Route}

trait BaseRouter extends Directives with CirceImplicits with Matchers {

    def routes: Route
}
