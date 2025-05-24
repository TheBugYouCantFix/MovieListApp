package app.domain

import io.circe.*
import io.circe.generic.semiauto.*

sealed trait Error(message: String) derives Codec.AsObject
case class MovieError(message: String) extends Error(message)
case class NoMovieWithGivenIdError(message: String = "No movie with given id found") extends Error(message)

