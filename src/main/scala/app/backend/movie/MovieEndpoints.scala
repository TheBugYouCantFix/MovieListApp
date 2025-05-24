package app.backend.movie

import app.backend.data.repositories.MovieRepo
import app.domain.{Error, ID, Movie}

import sttp.tapir.ztapir.*
import sttp.tapir.json.circe.*
import sttp.tapir.generic.auto.*

object MovieEndpoints:
  val add = endpoint
    .post
    .in(jsonBody[Movie])
    .errorOut(jsonBody[Error])
    .zServerLogic(MovieHandlers.addMovieHandler)

  val getById = endpoint
    .get
    .in("movie" / path[Long])
    .out(jsonBody[Movie])
    .errorOut(jsonBody[Error])
    .zServerLogic(MovieHandlers.getMovieByIdHandler)

  val update = endpoint
    .put
    .in(path[ID])
    .in(jsonBody[Movie])
    .errorOut(jsonBody[Error])
    .zServerLogic(MovieHandlers.updateMovieHandler)

  val delete = endpoint
    .delete
    .in("deleteMovie" / path[Long])
    .errorOut(jsonBody[Error])
    .zServerLogic(MovieHandlers.removeMovieByIdHandler)

  val getAll = endpoint
    .get
    .out(jsonBody[Vector[Movie]])
    .errorOut(jsonBody[Error])
    .zServerLogic(_ => MovieHandlers.getAllMoviesHandler)

  val endpoints = List(add, getById, update, delete, getAll)