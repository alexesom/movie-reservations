package utils

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.util.Helpers.Requiring

trait Handlers {

  implicit def rejectionHandler: RejectionHandler =
    RejectionHandler.newBuilder()
      .handleNotFound {
        complete((StatusCodes.NotFound, "Resource not found"))
      }
      .handle {
        case ValidationRejection(msg, _) =>
          complete((StatusCodes.BadRequest, msg))
        case MissingCookieRejection(cookieName) =>
          complete((StatusCodes.BadRequest, s"Missing cookie: $cookieName"))
        case AuthorizationFailedRejection =>
          complete((StatusCodes.Forbidden, "You're not authorized!"))
        case MalformedRequestContentRejection(msg, _) =>
          complete((StatusCodes.BadRequest, s"The request content was malformed: $msg"))
        case UnacceptedResponseContentTypeRejection(supported) =>
          complete((StatusCodes.NotAcceptable, s"Supported content types: ${supported.map(_.value).mkString(", ")}"))
      }
      .result()

  implicit def exceptionHandler: ExceptionHandler = ExceptionHandler {
    case ex: IllegalArgumentException =>
      complete((StatusCodes.BadRequest, s"Invalid input"))
    case ex: NoSuchElementException =>
      complete((StatusCodes.NotFound, s"Resource not found"))
    case ex: Exception =>
      complete((StatusCodes.InternalServerError, s"An unexpected error occurred"))
  }

}

