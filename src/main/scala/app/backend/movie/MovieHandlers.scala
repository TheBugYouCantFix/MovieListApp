package app.backend.movie

import app.backend.data.repositories.MovieRepo
import app.domain
import app.backend.AppEnv
import app.domain.{MovieId, Movie, MovieError, NoMovieWithGivenIdError}
import zio.*

object MovieHandlers:
  def addMovieHandler(movie: Movie): ZIO[AppEnv, domain.Error, Unit] =
    ZIO.serviceWithZIO[MovieRepo](_.add(movie)).mapError(e => MovieError(e.getMessage))
  
  def getMovieByIdHandler(id: MovieId): ZIO[AppEnv, domain.Error, Movie] = 
    ZIO.serviceWithZIO[MovieRepo](_.getById(id))
      .foldZIO(
        err => ZIO.fail(MovieError("Something went wrong")),
        {
          case Some(x) => ZIO.succeed(x)
          case None => ZIO.fail(NoMovieWithGivenIdError())
        }
      )
  
  def getAllMoviesHandler: ZIO[AppEnv, domain.Error, Vector[Movie]] =
    ZIO.serviceWithZIO[MovieRepo](_.getAll).mapError(e => MovieError(e.getMessage))
    
  def updateMovieHandler(id: MovieId, movie: Movie): ZIO[AppEnv, domain.Error, Unit] =
    ZIO.serviceWithZIO[MovieRepo](_.updateTo(id, movie)).mapError(e => MovieError(e.getMessage))

  def removeMovieByIdHandler(id: MovieId): ZIO[AppEnv, domain.Error, Unit] =
    ZIO.serviceWithZIO[MovieRepo](_.removeById(id)).mapError(e => MovieError(e.getMessage))
