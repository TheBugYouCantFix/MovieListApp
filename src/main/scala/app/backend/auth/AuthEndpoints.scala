package app.backend.auth

import sttp.tapir.ztapir.*
import sttp.tapir.json.circe.*
import sttp.tapir.generic.auto.*
import app.backend.auth.request_models.{DeleteUser, UpdatePassword}
import app.backend.data.repositories.UserRepo
import app.domain.{Credentials, Error}
import app.utils.given

object AuthEndpoints:
  val login = endpoint
    .post
    .in(jsonBody[Credentials])
    .errorOut(jsonBody[Error])
  val signup = endpoint
    .post
    .in(jsonBody[Credentials])
    .errorOut(jsonBody[Error])
  val updateUsername = endpoint
    .put
    .in(jsonBody[UpdatePassword])
    .errorOut(jsonBody[Error])
  val updatePassword = endpoint
    .put
    .in(jsonBody[Error])
    .errorOut(jsonBody[Error])
  val deleteAccount = endpoint
    .delete
    .in(jsonBody[DeleteUser])
    .errorOut(jsonBody[Error])

  val endpoints = List(login, signup, updateUsername, updatePassword, deleteAccount)