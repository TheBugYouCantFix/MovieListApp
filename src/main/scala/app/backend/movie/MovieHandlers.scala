package app.backend.movie

import app.backend.data.repositories.MovieRepo
import app.domain
import app.domain.{ID, Movie, MovieError, NoMovieWithGivenIdError}
import zio.*

object MovieHandlers:
  def addMovieHandler(movie: Movie): ZIO[MovieRepo, domain.Error, Unit] =
  
  def getMovieByIdHandler(id: ID): ZIO[MovieRepo, domain.Error, Movie] = 
    ZIO.serviceWithZIO[MovieRepo](_.getById(id))
      .foldZIO(
        err => ZIO.fail(MovieError("Something went wrong")),
        {
          case Some(x) => ZIO.succeed(x)
          case None => ZIO.fail(NoMovieWithGivenIdError())
        }
      )
  
  def getAllMoviesHandler: ZIO[MovieRepo, domain.Error, Vector[Movie]] =
    ZIO.serviceWithZIO[MovieRepo](_.getAll).mapError(e => MovieError(e.getMessage))
    
  def updateMovieHandler(id: ID, movie: Movie): ZIO[MovieRepo, domain.Error, Unit] =
    ZIO.serviceWithZIO[MovieRepo](_.updateTo(id, movie)).mapError(e => MovieError(e.getMessage))

  def removeMovieByIdHandler(id: ID): ZIO[MovieRepo, domain.Error, Unit] =
    ZIO.serviceWithZIO[MovieRepo](_.removeById(id)).mapError(e => MovieError(e.getMessage))
