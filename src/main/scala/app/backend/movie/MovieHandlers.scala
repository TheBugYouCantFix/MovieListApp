package app.backend.movie

import app.backend.data.repositories.MovieRepo
import app.domain
import app.backend.AppEnv
import app.backend.auth.jwt.JwtService
import app.domain.{AuthError, Error, Movie, MovieError, MovieId, NoMovieWithGivenIdError, UserId}
import zio.*

object MovieHandlers:
  def authenticateUser(token: String): ZIO[JwtService, Error, UserId] =
    ZIO.serviceWithZIO[JwtService](_.authenticateUser(token)).mapError(e => AuthError(e.getMessage))
  
  def addMovieHandler(userId: UserId)(movieReq: MovieRequest):  ZIO[AppEnv, domain.Error, Unit] =
    ZIO.serviceWithZIO[MovieRepo](_.add(movieReq.toMovie(userId))).mapError(e => MovieError(e.getMessage))
  

  def getMovieByIdHandler(userId: UserId)(id: MovieId): ZIO[AppEnv, domain.Error, Movie] = 
    ZIO.serviceWithZIO[MovieRepo](_.getById(id))
      .foldZIO(
        err => ZIO.fail(MovieError("Something went wrong")),
        {
          case Some(x) => ZIO.succeed(x)
          case None => ZIO.fail(NoMovieWithGivenIdError())
        }
      )
  
  def getAllMoviesHandler(userId: UserId): Unit => ZIO[AppEnv, domain.Error, Vector[Movie]] =
    _ => ZIO.serviceWithZIO[MovieRepo](_.getAll(userId)).mapError(e => MovieError(e.getMessage))
    
  def updateMovieHandler(userId: UserId)(id: MovieId, movieReq: MovieRequest): ZIO[AppEnv, domain.Error, Unit] =
    ZIO.serviceWithZIO[MovieRepo](_.updateTo(id, movieReq.toMovie(userId))).mapError(e => MovieError(e.getMessage))

  def removeMovieByIdHandler(userId: UserId)(id: MovieId): ZIO[AppEnv, domain.Error, Unit] =
    ZIO.serviceWithZIO[MovieRepo](_.removeById(id)).mapError(e => MovieError(e.getMessage))
