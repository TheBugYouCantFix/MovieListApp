package backend

import zio.*
import zio.http.*

import sttp.tapir.*
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter

import movie.MovieEndpoints
import data.dbLayer
import data.repositories.{MovieRepo, MovieRepoLive}

object MovieListApp extends ZIOAppDefault:
  val swaggerEndpoints =
    SwaggerInterpreter()
      .fromServerEndpoints(
        MovieEndpoints.endpoints,
        "MovieListApp",
        "1.0"
      )

  val app = 
    ZioHttpInterpreter().toHttp(
      MovieEndpoints.endpoints ++ swaggerEndpoints
    )
  

  override def run =
    Server
      .serve(app)
      .provide(
        ZLayer.succeed(Server.Config.default.port(8080)),
        MovieRepoLive.layer,
        dbLayer,
        Server.live
      )
      .exitCode