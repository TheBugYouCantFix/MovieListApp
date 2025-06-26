package app.backend.movie

import io.circe.generic.auto.*
import sttp.tapir.ztapir.*
import sttp.tapir.json.circe.*
import sttp.tapir.generic.auto.*
import io.github.iltotore.iron.circe.given
import app.backend.auth.AuthHandlers
import app.backend.commons.AppEnv
import app.backend.db.repositories.MovieRepo
import app.domain.{Error, Movie, MovieId, given}
import app.utils.given

object MovieEndpoints:
  val movieEdndpoint = endpoint
    .in("my" / "movie")
    .securityIn(auth.bearer[String]())

  val add = movieEdndpoint 
    .post
    .in("add")
    .in(jsonBody[MovieRequest])
    .errorOut(jsonBody[Error])
    .zServerSecurityLogic(MovieHandlers.authenticateUser)
    .serverLogic(MovieHandlers.addMovieHandler)

  val getById = movieEdndpoint 
    .get
    .in("get")
    .in(path[MovieId])
    .out(jsonBody[Movie])
    .errorOut(jsonBody[Error])
    .zServerSecurityLogic(MovieHandlers.authenticateUser)
    .serverLogic(MovieHandlers.getMovieByIdHandler)

  val update = movieEdndpoint 
    .put
    .in("update")
    .in(path[MovieId])
    .in(jsonBody[MovieRequest])
    .errorOut(jsonBody[Error])
    .zServerSecurityLogic(MovieHandlers.authenticateUser)
    .serverLogic(MovieHandlers.updateMovieHandler)

  val delete = movieEdndpoint 
    .delete
    .in("delete" / path[MovieId])
    .errorOut(jsonBody[Error])
    .zServerSecurityLogic(MovieHandlers.authenticateUser)
    .serverLogic(MovieHandlers.removeMovieByIdHandler)

  val getAll = movieEdndpoint 
    .get
    .in("getAll")
    .out(jsonBody[Vector[Movie]])
    .errorOut(jsonBody[Error])
    .zServerSecurityLogic(MovieHandlers.authenticateUser)
    .serverLogic(MovieHandlers.getAllMoviesHandler)

  val endpoints: List[ZServerEndpoint[AppEnv, Any]] = List(add, getById, update, delete, getAll)