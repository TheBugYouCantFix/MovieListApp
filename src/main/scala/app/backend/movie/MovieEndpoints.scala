package app.backend.movie

import app.backend.AppEnv
import app.backend.data.repositories.MovieRepo
import app.domain.{Error, Movie, MovieId, given}
import app.utils.given
import sttp.tapir.ztapir.*
import sttp.tapir.json.circe.*
import sttp.tapir.generic.auto.*
import io.github.iltotore.iron.circe.given

object MovieEndpoints:
  val add = endpoint
    .post
    .in("addMovie")
    .in(jsonBody[Movie])
    .errorOut(jsonBody[Error])
    .zServerLogic(MovieHandlers.addMovieHandler)

  val getById = endpoint
    .get
    .in("getMovie")
    .in(path[MovieId])
    .out(jsonBody[Movie])
    .errorOut(jsonBody[Error])
    .zServerLogic(MovieHandlers.getMovieByIdHandler)

  val update = endpoint
    .put
    .in("updateMovie")
    .in(path[MovieId])
    .in(jsonBody[Movie])
    .errorOut(jsonBody[Error])
    .zServerLogic(MovieHandlers.updateMovieHandler)

  val delete = endpoint
    .delete
    .in("deleteMovie" / path[MovieId])
    .errorOut(jsonBody[Error])
    .zServerLogic(MovieHandlers.removeMovieByIdHandler)

  val getAll = endpoint
    .get
    .in("getAllMovies")
    .out(jsonBody[Vector[Movie]])
    .errorOut(jsonBody[Error])
    .zServerLogic(_ => MovieHandlers.getAllMoviesHandler)

  val endpoints: List[ZServerEndpoint[AppEnv, Any]] = List(add, getById, update, delete, getAll)