package app.tables

import app.domain
import app.domain.ID
import com.augustnagro.magnum.*


@Table(PostgresDbType, SqlNameMapper.CamelToSnakeCase)
final case class Movie(
                  @Id movieId: ID,
                  uid: ID, // ID of a user which the movie is associated with
                  name: String,
                  rating: Int,
                  review: String
                ) derives DbCodec:
  val toDomain: domain.Movie = domain.Movie(uid, name, rating, review)

object Movie:
  def fromDomain(id: ID, movie: domain.Movie): Movie = 
    Movie(id, movie.uid, movie.name, movie.rating, movie.review)