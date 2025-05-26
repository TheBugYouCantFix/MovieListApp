package app.backend.auth

import app.backend.data.repositories.MovieRepo
import app.domain.{Error, ID, Movie}

import sttp.tapir.ztapir.*
import sttp.tapir.json.circe.*
import sttp.tapir.generic.auto.*


object AuthEndpoints:
  val login = ???
  val signup = ???
  val updateUsername= ???
  val updatePassword = ???
  val deleteAccount = ???
  
  val endpoints = List(login, signup, updateUsername, updatePassword, deleteAccount)