package app.backend.movie

import io.circe.generic.auto.*
import sttp.tapir.ztapir.*
import sttp.tapir.json.circe.*
import sttp.tapir.generic.auto.*
import io.github.iltotore.iron.circe.given

import app.backend.AppEnv
import app.backend.auth.AuthHandlers
import app.backend.db.repositories.MovieRepo
import app.domain.{Error, Movie, MovieId, given}
import app.utils.given

object MovieEndpoints:
  val secureEndpoint = endpoint 
    .securityIn(auth.bearer[String]())

  val add = secureEndpoint 
    .post
    .in("addMovie")
    .in(jsonBody[MovieRequest])
    .errorOut(jsonBody[Error])
    .zServerSecurityLogic(MovieHandlers.authenticateUser)
    .serverLogic(MovieHandlers.addMovieHandler)

  val getById = secureEndpoint 
    .get
    .in("getMovie")
    .in(path[MovieId])
    .out(jsonBody[Movie])
    .errorOut(jsonBody[Error])
    .zServerSecurityLogic(MovieHandlers.authenticateUser)
    .serverLogic(MovieHandlers.getMovieByIdHandler)

  val update = secureEndpoint
    .put
    .in("updateMovie")
    .in(path[MovieId])
    .in(jsonBody[MovieRequest])
    .errorOut(jsonBody[Error])
    .zServerSecurityLogic(MovieHandlers.authenticateUser)
    .serverLogic(MovieHandlers.updateMovieHandler)

  val delete = secureEndpoint
    .delete
    .in("deleteMovie" / path[MovieId])
    .errorOut(jsonBody[Error])
    .zServerSecurityLogic(MovieHandlers.authenticateUser)
    .serverLogic(MovieHandlers.removeMovieByIdHandler)

  val getAll = secureEndpoint
    .get
    .in("getAllMovies")
    .out(jsonBody[Vector[Movie]])
    .errorOut(jsonBody[Error])
    .zServerSecurityLogic(MovieHandlers.authenticateUser)
    .serverLogic(MovieHandlers.getAllMoviesHandler)

  val endpoints: List[ZServerEndpoint[AppEnv, Any]] = List(add, getById, update, delete, getAll)