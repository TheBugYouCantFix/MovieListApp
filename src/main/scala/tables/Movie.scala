package tables

import com.augustnagro.magnum.*

import domain.*


@Table(PostgresDbType, SqlNameMapper.CamelToSnakeCase)
case class Movie(
                @Id movieId: domain.ID,
                uid: domain.ID, // ID of a user which the movie is associated with
                name: String,
                rating: Int,
                review: String
                ) derives DbCodec:
  val toDomain: domain.Movie = domain.Movie(uid, name, rating, review)

object Movie:
  def fromDomain(id: domain.ID, movie: domain.Movie): Movie = 
    Movie(id, movie.uid, movie.name, movie.rating, movie.review)