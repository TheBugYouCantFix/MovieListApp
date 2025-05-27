package app.tables

import com.augustnagro.magnum.*

import app.domain
import app.domain.MovieId
import app.utils.given

@Table(PostgresDbType, SqlNameMapper.CamelToSnakeCase)
final case class Movie(
                  @Id movieId: MovieId,
                  uid: MovieId, // ID of a user which the movie is associated with
                  name: String,
                  rating: Int,
                  review: String
                ) derives DbCodec:
  val toDomain: domain.Movie = domain.Movie(uid, name, rating, review)

object Movie:
  def fromDomain(id: MovieId, movie: domain.Movie): Movie = 
    Movie(id, movie.uid, movie.name, movie.rating, movie.review)