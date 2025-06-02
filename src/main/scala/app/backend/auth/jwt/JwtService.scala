package app.backend.auth.jwt

import app.domain.UserId
import pdi.jwt.{JwtAlgorithm, JwtCirce, JwtClaim}

import java.time.Instant
import scala.util.Try

case class JwtService(jwtConfig: JwtConfig):
  def jwtEncode(userId: UserId): String =
    JwtCirce.encode(
      JwtClaim(subject = Some(userId.))
    )  

  def jwtDecode(token: String): Try[JwtClaim] = JwtCirce.decode(
    token, jwtConfig.key, Seq(jwtConfig.algorithm)
  )
