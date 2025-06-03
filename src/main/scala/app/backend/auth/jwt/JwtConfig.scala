package app.backend.auth.jwt

import pdi.jwt.{JwtAlgorithm, JwtClaim}

import javax.crypto.spec.SecretKeySpec
import java.time.Instant
import app.domain.UserId
import pdi.jwt.JwtAlgorithm.HS512
import zio.{RLayer, ZLayer}

case class JwtConfig(
                      userId: UserId,
                      key: String,
                      algorithm: JwtAlgorithm
                    ):
    val claim: JwtClaim = JwtClaim(
      subject = Some(userId.toString),
      expiration = Some(Instant.now.plusSeconds(900).getEpochSecond),
      issuedAt = Some(Instant.now.getEpochSecond)
    )

    val secretKey: SecretKeySpec = new SecretKeySpec(
      key.getBytes("UTF-8"), algorithm.name
    )

object JwtConfig:
  private val defaultKey: String = "change-me-in-production"
  private val defaultAlgorithm: JwtAlgorithm = HS512
  private type Env = UserId & String & JwtAlgorithm

  val customLayer: RLayer[Env, JwtConfig] =
    ZLayer.fromFunction(JwtConfig(_, _, _))

  val defaultLayer: RLayer[UserId, JwtConfig] =
    ZLayer.fromFunction(JwtConfig(_, defaultKey, defaultAlgorithm))