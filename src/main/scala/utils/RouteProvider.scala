package utils

import akka.http.scaladsl.server.{Directives, Route, RouteConcatenation}
import akka.http.scaladsl.server.directives.RouteDirectives

trait RouteProvider extends Directives with RouteConcatenation {
  def route: Route = RouteDirectives.reject
}
