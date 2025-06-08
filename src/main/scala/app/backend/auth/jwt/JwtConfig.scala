package app.backend.auth.jwt

import pdi.jwt.{JwtAlgorithm, JwtClaim}

import javax.crypto.spec.SecretKeySpec
import java.time.Instant
import app.domain.UserId
import pdi.jwt.JwtAlgorithm.HS512
import zio.{RLayer, ULayer, ZLayer}

case class JwtConfig(
                      key: String,
                      algorithm: JwtAlgorithm,
                      durationInSecs: Int
                    ):
    def claim(userId: UserId): JwtClaim = JwtClaim(
      subject = Some(userId.toString),
      expiration = Some(Instant.now.plusSeconds(durationInSecs).getEpochSecond),
      issuedAt = Some(Instant.now.getEpochSecond)
    )

    val secretKey: SecretKeySpec = new SecretKeySpec(
      key.getBytes("UTF-8"), algorithm.name
    )

object JwtConfig:
  private val defaultKey: String = "change-me-in-production"
  private val defaultAlgorithm: JwtAlgorithm = HS512
  private val defaultDurationInSecs: Int = 3600
  
  private type Env = String & JwtAlgorithm & Int

  val customLayer: RLayer[Env, JwtConfig] =
    ZLayer.fromFunction(JwtConfig(_, _, _))

  val defaultLayer: ULayer[JwtConfig] =
    ZLayer.succeed(JwtConfig(defaultKey, defaultAlgorithm, defaultDurationInSecs))