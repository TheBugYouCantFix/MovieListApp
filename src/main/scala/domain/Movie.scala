package domain

import io.circe.*
import io.circe.generic.semiauto.*

case class Movie(
                  uid: Long, // ID of a user which the movie is associated with
                  name: String,
                  rating: Int,
                  review: String
                ) derives Codec.AsObject

