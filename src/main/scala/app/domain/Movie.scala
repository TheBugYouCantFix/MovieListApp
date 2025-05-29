package app.domain

import io.circe.*
import io.circe.generic.semiauto.*
import io.github.iltotore.iron.circe.*

import sttp.tapir.codec.iron.given
import sttp.tapir.Schema

import app.domain.MovieId

given Encoder[MovieId] = Encoder[Long].contramap(identity)
given Decoder[MovieId] = Decoder[Long].emap(MovieId.either)
given Schema[MovieId] = Schema.schemaForLong.as[MovieId]

case class Movie(
                  uid: MovieId, // ID of a user which the movie is associated with
                  name: String,
                  rating: Int,
                  review: String
                ) derives Codec.AsObject

