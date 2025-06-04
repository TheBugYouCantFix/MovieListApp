package app.domain

import io.circe.*

sealed trait Error(message: String) extends Throwable derives Codec.AsObject

case class MovieError(message: String) extends Error(message)
case class NoMovieWithGivenIdError(message: String = "No movie with given id found") extends Error(message)


case class AuthError(message: String) extends Error(message)
case class PasswordHashingFailedError(message: String) extends Error(message)
case class InvalidCredentialsError(message: String = "Invalid credentials") extends Error(message)
case class NoUserWithGivenIdError(message: String = "No user with given id found") extends Error(message)