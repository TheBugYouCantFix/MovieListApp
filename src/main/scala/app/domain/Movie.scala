package app.domain

import io.circe.*
import io.circe.generic.semiauto.*

import app.domain.ID

case class Movie(
                  uid: ID, // ID of a user which the movie is associated with
                  name: String,
                  rating: Int,
                  review: String
                ) derives Codec.AsObject

