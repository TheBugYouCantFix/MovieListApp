package app.domain

import io.circe.*
import io.circe.generic.semiauto.*

import io.github.iltotore.iron.circe.given

import app.domain.UserId

case class Movie(
                  uid: UserId, // ID of a user which the movie is associated with
                  name: String,
                  rating: Int,
                  review: String
                ) derives Codec.AsObject

