package app.backend

import zio.*
import zio.http.*
import zio.http.Header.{AccessControlAllowOrigin, Origin}
import zio.http.Middleware.{CorsConfig, cors}
import sttp.tapir.*
import sttp.tapir.server.ziohttp.{ZioHttpInterpreter, ZioHttpServerOptions}
import sttp.tapir.server.interceptor.log.{DefaultServerLog, ServerLog}
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.tapir.server.interceptor.log.{DefaultServerLog, ServerLog}
import sttp.tapir.server.ziohttp.{ZioHttpInterpreter, ZioHttpServerOptions}
import movie.MovieEndpoints
import app.backend.auth.AuthEndpoints
import app.backend.auth.jwt.{JwtConfig, JwtService}
import data.dbLayer
import data.repositories.{MovieRepo, MovieRepoLive, UserRepo}
import zio.http.Header.AccessControlAllowOrigin.Specific

object MovieListApp extends ZIOAppDefault:
  val endpoints = AuthEndpoints.endpoints ++ MovieEndpoints.endpoints

  val swaggerEndpoints = ZioHttpInterpreter().toHttp( SwaggerInterpreter()
      .fromServerEndpoints(
        endpoints,
        "MovieListApp",
        "1.0"
      )
  )

  val appEndpoints: Routes[AppEnv, Response] =
      ZioHttpInterpreter().toHttp(
        endpoints
    ) ++ swaggerEndpoints

  val corsConfig = CorsConfig(
    allowedOrigin = {
      case origin if origin == Origin.parse("http://localhost:8080").toOption.get =>
        Some(AccessControlAllowOrigin.Specific(origin))
      case _ => None
    }
  )

  val app = appEndpoints @@ cors(corsConfig)

  override def run =
    Server.serve(app)
      .provide(
        ZLayer.succeed(Server.Config.default.port(8080)),
        dbLayer,
        JwtConfig.defaultLayer,
        MovieRepo.layer,
        UserRepo.layer,
        JwtService.layer,
        Server.live
      )