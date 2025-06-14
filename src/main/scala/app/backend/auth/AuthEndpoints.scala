package app.backend.auth

import app.backend.AppEnv
import app.backend.auth.requestmodels.{UpdatePasswordRequest, UpdateUsernameRequest}
import sttp.tapir.ztapir.*
import sttp.tapir.json.circe.*
import sttp.tapir.generic.auto.*
import app.domain.credentials.*
import app.domain.{Error, Password, UserId, Username}
import app.utils.given

object AuthEndpoints:
  val login = endpoint
    .post
    .in("login")
    .in(jsonBody[PreAuthCredentials])
    .out(plainBody[String])
    .errorOut(jsonBody[Error])
    .zServerLogic(AuthHandlers.loginHandler)

  val signup = endpoint
    .post
    .in("signup")
    .in(jsonBody[PreAuthCredentials])
    .out(plainBody[String])
    .errorOut(jsonBody[Error])
    .zServerLogic(AuthHandlers.signupHandler)

  val securedEndpoint = endpoint
    .securityIn(auth.bearer[String]())

  val updateUsername = securedEndpoint
    .put
    .in("updateUsername")
    .in(jsonBody[UpdateUsernameRequest])
    .errorOut(jsonBody[Error])
    .zServerSecurityLogic(AuthHandlers.authenticateUser)
    .serverLogic(AuthHandlers.updateUsernameHandler)

  val updatePassword = securedEndpoint
    .put
    .in("updatePassword")
    .in(jsonBody[UpdatePasswordRequest])
    .errorOut(jsonBody[Error])
    .zServerSecurityLogic(AuthHandlers.authenticateUser)
    .serverLogic(AuthHandlers.updatePasswordHandler)

  val deleteAccount = securedEndpoint
    .delete
    .in("deleteAccount")
    .in(jsonBody[Password])
    .errorOut(jsonBody[Error])
    .zServerSecurityLogic(AuthHandlers.authenticateUser)
    .serverLogic(AuthHandlers.deleteUserHandler)

  val authorizationEndpoints = List(login, signup)
  val securedEndpoints = List(updateUsername, updatePassword, deleteAccount)

  val allEndpoints = (authorizationEndpoints ++ securedEndpoints).map(_.widen[AppEnv])