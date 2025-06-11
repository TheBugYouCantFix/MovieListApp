package app.backend

import zio.*
import zio.http.*
import sttp.tapir.*
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import movie.MovieEndpoints
import app.backend.auth.AuthEndpoints
import app.backend.auth.jwt.{JwtConfig, JwtService}
import data.dbLayer
import data.repositories.{MovieRepo, MovieRepoLive, UserRepo}

object MovieListApp extends ZIOAppDefault:
  val swaggerEndpoints = ZioHttpInterpreter().toHttp(
    SwaggerInterpreter()
      .fromServerEndpoints(
        MovieEndpoints.endpoints ++ AuthEndpoints.allEndpoints,
        "MovieListApp",
        "1.0"
      )
  )
  val endpoints: Routes[AppEnv, Response] =
      ZioHttpInterpreter().toHttp(
        MovieEndpoints.endpoints ++ AuthEndpoints.allEndpoints
    )

  val app = (swaggerEndpoints ++ endpoints) @@ Middleware.debug

  override def run =
    Server.serve(app)
      .provide(
        ZLayer.succeed(Server.Config.default.port(8080)),
        MovieRepo.layer,
        UserRepo.layer,
        JwtConfig.defaultLayer,
        JwtService.layer,
        dbLayer,
        Server.live
      )
      .exitCode