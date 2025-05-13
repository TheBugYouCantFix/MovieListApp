package backend.movie

import io.circe.*
import io.circe.generic.semiauto.*

case class Movie(
                name: String,
                rating: Int,
                review: String
                ) derives Codec.AsObject


