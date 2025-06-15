package app.backend.auth

import sttp.tapir.ztapir.*
import sttp.tapir.json.circe.*
import sttp.tapir.generic.auto.*

import app.backend.auth.requestmodels.{UpdatePasswordRequest, UpdateUsernameRequest}
import app.domain.credentials.*
import app.domain.{Error, Password, UserId}
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

  val endpoints = List(login, signup, updateUsername, updatePassword, deleteAccount)