package app.domain

import io.circe.*
import io.circe.generic.semiauto.*

import app.domain.MovieId

case class Movie(
                  uid: MovieId, // ID of a user which the movie is associated with
                  name: String,
                  rating: Int,
                  review: String
                ) derives Codec.AsObject

