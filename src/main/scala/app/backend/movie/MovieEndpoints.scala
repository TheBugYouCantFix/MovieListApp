package app.backend.movie

import app.backend.data.repositories.MovieRepo
import app.domain.{Error, MovieId, Movie, given }
import app.utils.given 

import sttp.tapir.ztapir.*
import sttp.tapir.json.circe.*
import sttp.tapir.generic.auto.*

import io.github.iltotore.iron.circe.given
import sttp.tapir.codec.iron.*

object MovieEndpoints:
  val add = endpoint
    .post
    .in(jsonBody[Movie])
    .errorOut(jsonBody[Error])
    .zServerLogic(MovieHandlers.addMovieHandler)

  val getById = endpoint
    .get
    .in("movie" / path[MovieId])
    .out(jsonBody[Movie])
    .errorOut(jsonBody[Error])
    .zServerLogic(MovieHandlers.getMovieByIdHandler)

  val update = endpoint
    .put
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
    .out(jsonBody[Vector[Movie]])
    .errorOut(jsonBody[Error])
    .zServerLogic(_ => MovieHandlers.getAllMoviesHandler)

  val endpoints = List(add, getById, update, delete, getAll)