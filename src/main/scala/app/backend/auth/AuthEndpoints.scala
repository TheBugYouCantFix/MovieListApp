package app.backend.auth

import sttp.tapir.ztapir.*
import sttp.tapir.json.circe.*
import sttp.tapir.generic.auto.*
import app.backend.auth.requestmodels.{UpdatePassword, UpdateUsername}
import app.backend.data.repositories.UserRepo
import app.domain.{Credentials, Error}
import app.utils.given

object AuthEndpoints:
  val login = endpoint
    .post
    .in(jsonBody[Credentials])
    .out(plainBody[String])
    .errorOut(jsonBody[Error])
    .zServerLogic(AuthHandlers.loginHandler)

  val signup = endpoint
    .post
    .in(jsonBody[Credentials])
    .out(plainBody[String])
    .errorOut(jsonBody[Error])
    .zServerLogic(AuthHandlers.signupHandler)

  val updateUsername = endpoint
    .put
    .in(jsonBody[UpdateUsername])
    .errorOut(jsonBody[Error])
    .zServerLogic(AuthHandlers.updateUsernameHandler)

  val updatePassword = endpoint
    .put
    .in(jsonBody[UpdatePassword])
    .errorOut(jsonBody[Error])
    .zServerLogic(AuthHandlers.updatePasswordHandler)

  val deleteAccount = endpoint
    .delete
    .in(jsonBody[Credentials])
    .errorOut(jsonBody[Error])
    .zServerLogic(AuthHandlers.deleteUserHandler)

  val authorizationEndpoints = List(login, signup)
  val endpoints = List(updateUsername, updatePassword, deleteAccount)