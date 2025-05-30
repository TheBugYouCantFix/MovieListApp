package app.domain

import io.circe.*

import sttp.tapir.Schema

import app.domain.{MovieId, BaseIdType}

given Encoder[MovieId] = Encoder[BaseIdType].contramap(identity)
given Decoder[MovieId] = Decoder[BaseIdType].emap(MovieId.either)
given Schema[MovieId] = Schema.schemaForLong.as[MovieId]

case class Movie(
                  uid: MovieId, // ID of a user which the movie is associated with
                  name: String,
                  rating: Int,
                  review: String
                ) derives Codec.AsObject

