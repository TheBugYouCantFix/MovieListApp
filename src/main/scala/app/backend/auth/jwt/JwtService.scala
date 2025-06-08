package app.backend.auth.jwt

import pdi.jwt.algorithms.JwtHmacAlgorithm
import pdi.jwt.{JwtCirce, JwtClaim}

import zio.*
import zio.http.*

import scala.util.Try

import app.domain.UserId

trait JwtService:
  def jwtEncode(userId: UserId): Task[String]
  def jwtDecodeSync(token: String): Try[JwtClaim]
  def jwtDecode(token: String): Task[JwtClaim]
  val bearerAuthWithContext: HandlerAspect[Any, String]
  
case class JwtServiceLive(jwtConfig: JwtConfig) extends JwtService:
  def jwtEncode(userId: UserId): Task[String] = ZIO.succeed(
    JwtCirce.encode(jwtConfig.claim(userId), jwtConfig.secretKey, jwtConfig.algorithm.asInstanceOf[JwtHmacAlgorithm])
  )

  def jwtDecodeSync(token: String): Try[JwtClaim] =
    JwtCirce.decode(
      token, jwtConfig.secretKey, Seq(jwtConfig.algorithm.asInstanceOf[JwtHmacAlgorithm])
    ).orElse(
      JwtCirce.decode(token)
    )

  def jwtDecode(token: String): Task[JwtClaim] = ZIO.fromTry(
    jwtDecodeSync(token)
  )

  val bearerAuthWithContext: HandlerAspect[Any, String] =
    HandlerAspect.interceptIncomingHandler(
      Handler.fromFunctionZIO[Request] {
        req => req.header(Header.Authorization) match
          case Some(Header.Authorization.Bearer(token)) =>
            jwtDecode(token.value.asString)
              .orElseFail(Response.badRequest("Invalid or expired token!"))
              .flatMap(
                claim => ZIO.fromOption(claim.subject)
                  .orElseFail(Response.badRequest("Missing subject claim!"))
              )
              .map(u => (req, u))

          case _ => ZIO.fail(Response.unauthorized.addHeader(
            Header.WWWAuthenticate.Bearer(realm = "Access")
          ))
      }
    )
    
object JwtService:
  val layer: RLayer[JwtConfig, JwtServiceLive] =
    ZLayer.fromFunction(JwtServiceLive(_))