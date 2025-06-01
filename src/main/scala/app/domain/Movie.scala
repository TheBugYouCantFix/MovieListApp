package app.domain

import io.circe.*

import app.domain.MovieId
import app.utils.given 

case class Movie(
                  uid: MovieId, // ID of a user which the movie is associated with
                  name: String,
                  rating: Int,
                  review: String
                ) derives Codec.AsObject

