package app.backend.auth

import app.backend.AppEnv
import sttp.tapir.ztapir.*
import sttp.tapir.json.circe.*
import sttp.tapir.generic.auto.*
import sttp.tapir.codec.iron.*
import app.backend.data.repositories.UserRepo
import app.domain.{Credentials, Error, Password, UserId, Username}
import app.utils.given
import zio.RIO

object AuthEndpoints:
  val login = endpoint
    .post
    .in("login")
    .in(jsonBody[Credentials])
    .out(plainBody[String])
    .errorOut(jsonBody[Error])
    .zServerLogic(AuthHandlers.loginHandler)

  val signup = endpoint
    .post
    .in("signup")
    .in(jsonBody[Credentials])
    .out(plainBody[String])
    .errorOut(jsonBody[Error])
    .zServerLogic(AuthHandlers.signupHandler)

  val securedEndpoint = endpoint
    .securityIn(auth.bearer[String]())

  val updateUsername = securedEndpoint
    .put
    .in("updateUsername")
    .in(jsonBody[Username])
    .errorOut(jsonBody[Error])
    .zServerSecurityLogic(AuthHandlers.authenticateUser)
    .serverLogic(AuthHandlers.updateUsernameHandler)

  val updatePassword = securedEndpoint
    .put
    .in("updatePassword")
    .in(jsonBody[Password])
    .errorOut(jsonBody[Error])
    .zServerSecurityLogic(AuthHandlers.authenticateUser)
    .serverLogic(AuthHandlers.updatePasswordHandler)

  val deleteAccount = securedEndpoint
    .delete
    .in("deleteAccount")
    .errorOut(jsonBody[Error])
    .zServerSecurityLogic(AuthHandlers.authenticateUser)
    .serverLogic(AuthHandlers.deleteUserHandler)

  val authorizationEndpoints = List(login, signup)
  val securedEndpoints = List(updateUsername, updatePassword, deleteAccount)

  // For combining all endpoints, widen to a common type
  val allEndpoints = (authorizationEndpoints ++ securedEndpoints).map(_.widen[AppEnv])