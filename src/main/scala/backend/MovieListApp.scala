package backend

import zio.*
import zio.http.*

import sttp.tapir.*
import sttp.tapir.server.netty.NettyFutureServer
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.tapir.ztapir.ZServerEndpoint

import movie.MovieEndpoints

object MovieListApp extends ZIOAppDefault:
  val swaggerEndpoints =
    SwaggerInterpreter()
      .fromServerEndpoints(
        MovieEndpoints.endpoints,
        "MovieListApp",
        "1.0"
      )

  val app: Routes[Any, Response] = 
    ZioHttpInterpreter().toHttp(
      MovieEndpoints.endpoints ++ swaggerEndpoints
    )
  

  override def run =
    Server
      .serve(app)
      .provide(
        ZLayer.succeed(Server.Config.default.port(8080)),
        Server.live
      )
      .exitCode