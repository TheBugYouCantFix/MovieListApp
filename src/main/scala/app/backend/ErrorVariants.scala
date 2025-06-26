package api

import app.domain.{
  MovieError,
  UserError,
  AuthError,
  DbError
}

import io.circe.generic.auto.*
import sttp.model.StatusCode
import sttp.tapir.generic.auto.*
import sttp.tapir.json.circe.*
import sttp.tapir.ztapir.*

object ErrorVariants:
  val movieNotFoundVariant =
    oneOfVariant(statusCode(StatusCode.NotFound).and(jsonBody[MovieError.NotFound]))

  val userNotFoundVariant =
    oneOfVariant(statusCode(StatusCode.NotFound).and(jsonBody[UserError.NotFound]))

  val invalidCredentialsVariant =
    oneOfVariant(statusCode(StatusCode.Unauthorized).and(jsonBody[AuthError.InvalidCredentials]))

  val passwordHashingFailedError =
    oneOfVariant(statusCode(StatusCode.InternalServerError).and(jsonBody[AuthError.PasswordHashingFailed]))

val dbErrorVariant =
  oneOfVariant(statusCode(StatusCode.InternalServerError).and(jsonBody[DbError.UnexpectedDbError]))

  