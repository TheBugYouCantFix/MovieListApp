package backend.movie

import zio.*

import backend.data.repositories.MovieRepo
import domain.*

object MovieHandlers:
  def addMovieHandler(movie: Movie): ZIO[MovieRepo, Error, Unit] =
    ZIO.serviceWithZIO[MovieRepo](_.add(movie)).mapError(e => MovieError(e.getMessage))
  
  def getMovieByIdHandler(id: ID): ZIO[MovieRepo, Error, Movie] = 
    ZIO.serviceWithZIO[MovieRepo](_.getById(id))
      .foldZIO(
        err => ZIO.fail(MovieError("Something went wrong")),
        {
          case Some(x) => ZIO.succeed(x)
          case None => ZIO.fail(NoMovieWithGivenIdError())
        }
      )
  
  def getAllMoviesHandler: ZIO[MovieRepo, Error, Vector[Movie]] =
    ZIO.serviceWithZIO[MovieRepo](_.getAll).mapError(e => MovieError(e.getMessage))
    
  def updateMovieHandler(id: ID, movie: Movie): ZIO[MovieRepo, Error, Unit] =
    ZIO.serviceWithZIO[MovieRepo](_.updateMovie(id, movie)).mapError(e => MovieError(e.getMessage))

  def removeMovieByIdHandler(id: ID): ZIO[MovieRepo, Error, Unit] =
    ZIO.serviceWithZIO[MovieRepo](_.removeById(id)).mapError(e => MovieError(e.getMessage))
