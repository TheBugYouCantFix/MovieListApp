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
  val securedEndpoints: ZIO[JwtService, Nothing, Routes[AppEnv, Response]] = ZIO.serviceWithZIO[JwtService] { jwtService =>
    ZIO.succeed(
      ZioHttpInterpreter().toHttp(
        MovieEndpoints.endpoints ++ AuthEndpoints.endpoints
      ) @@ jwtService.bearerAuthWithContext
    )
  }
  val authorizationEndpoints: Routes[AppEnv, Response] =
    ZioHttpInterpreter()
      .toHttp(AuthEndpoints.authorizationEndpoints)
  val app = securedEndpoints.map(_ ++ swaggerEndpoints ++ authorizationEndpoints)

  override def run =
    app.flatMap(
      Server.serve(_)
      )
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