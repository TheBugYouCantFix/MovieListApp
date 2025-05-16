package domain

import io.circe.*
import io.circe.generic.semiauto.*

case class MovieSlug(value: String) extends AnyVal

case class Movie(
                userId: Long, // ID of a user which the movie is associated with
                name: String,
                rating: Int,
                review: String
                ) derives Codec.AsObject


