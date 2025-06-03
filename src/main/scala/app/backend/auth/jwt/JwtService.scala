package app.backend.auth.jwt

import pdi.jwt.algorithms.JwtHmacAlgorithm
import pdi.jwt.{JwtCirce, JwtClaim}

import scala.util.Try

case class JwtService(jwtConfig: JwtConfig):
  def jwtEncode: String = JwtCirce.encode(jwtConfig.claim)

  def jwtDecode(token: String): Try[JwtClaim] = JwtCirce.decode(
    token, jwtConfig.secretKey, Seq(jwtConfig.algorithm.asInstanceOf[JwtHmacAlgorithm])
  )
