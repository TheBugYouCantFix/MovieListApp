package app.backend.db.tables

import com.augustnagro.magnum.*

import app.domain
import app.domain.{MovieId, UserId}
import app.utils.given

@Table(PostgresDbType, SqlNameMapper.CamelToSnakeCase)
final case class Movies(
                  @Id movieId: MovieId,
                  uid: UserId, // ID of a user which the movie is associated with
                  name: String,
                  rating: Int,
                  review: String
                ) derives DbCodec:
  val toDomain: domain.Movie = domain.Movie(uid, name, rating, review)

object Movies:
  val table = TableInfo[domain.Movie, Movies, MovieId]
  
  def fromDomain(id: MovieId, movie: domain.Movie): Movies = 
    Movies(id, movie.uid, movie.name, movie.rating, movie.review)