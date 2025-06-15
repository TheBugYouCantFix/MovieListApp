package app.backend.auth.jwt

import pdi.jwt.algorithms.JwtHmacAlgorithm
import pdi.jwt.{JwtCirce, JwtClaim}

import io.github.iltotore.iron.assume

import zio.*
import zio.http.*

import scala.util.Try

import app.domain.{UserId, UserIdDescription}

trait JwtService:
  def jwtEncode(userId: UserId): Task[String]
  def jwtDecodeSync(token: String): Try[JwtClaim]
  def jwtDecode(token: String): Task[JwtClaim]
  
case class JwtServiceLive(jwtConfig: JwtConfig) extends JwtService:
  def jwtEncode(userId: UserId): Task[String] = ZIO.succeed(
    JwtCirce.encode(jwtConfig.claim(userId), jwtConfig.secretKey, jwtConfig.algorithm.asInstanceOf[JwtHmacAlgorithm])
  )

  def jwtDecodeSync(token: String): Try[JwtClaim] =
    JwtCirce.decode(
      token, jwtConfig.secretKey, Seq(jwtConfig.algorithm.asInstanceOf[JwtHmacAlgorithm])
    )

  def jwtDecode(token: String): Task[JwtClaim] = ZIO.fromTry(
    jwtDecodeSync(token)
  )

object JwtService:
  val layer: RLayer[JwtConfig, JwtServiceLive] =
    ZLayer.fromFunction(JwtServiceLive(_))