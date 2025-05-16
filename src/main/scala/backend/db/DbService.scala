package backend.db

import io.getquill.*
import io.getquill.jdbczio.Quill

import zio.{Task, ZIO, ZLayer}
import domain.{Movie, User}

class DbService(quill: Quill.Postgres[SnakeCase]) {
  import quill.*

  def addMovie(movie: Movie): Task[Movie] =
    run {
      quote {
        query[Movie].insertValue(lift(movie)).returning(m => m)
      }
    }

  def getMoviesByUserId(userId: Long): Task[List[Movie]] =
    run {
      quote {
       query[Movie].filter(_.userId == lift(userId))
      }
    }

  def updateMovie(movie: Movie): Task[Movie] =
    run {
      quote {
        query[Movie]
          .filter(_.userId == lift(movie.userId))
          .updateValue(lift(movie))
          .returning(u => u)
      }
    }
    
  def deleteMovie(movie: Movie): Task[Unit] =
    run {
      quote {
        query[Movie]
          .filter(_.userId == lift(movie.userId))
          .delete
      }
    }.unit
}
