package domain

import com.augustnagro.magnum.*

import io.circe.*
import io.circe.generic.semiauto.*

case class MovieSlug(value: String) extends AnyVal

@Table(PostgresDbType, SqlNameMapper.CamelToSnakeCase)
case class Movie(
                @Id movieId: ID,
                uid: Long, // ID of a user which the movie is associated with
                name: String,
                rating: Int,
                review: String
                ) derives DbCodec, Codec.AsObject

val movieRepo = Repo[Movie, Movie, Long]
