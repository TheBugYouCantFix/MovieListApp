package app.backend.auth.jwt

import app.backend.auth.requestmodels.UpdateUsername
import pdi.jwt.{JwtAlgorithm, JwtClaim}

import javax.crypto.spec.SecretKeySpec
import java.time.Instant
import app.domain.Username
import pdi.jwt.JwtAlgorithm.HS512
import zio.{RLayer, ULayer, ZLayer}

case class JwtConfig(
                      key: String,
                      algorithm: JwtAlgorithm,
                      durationInSecs: Int
                    ):
    def claim(username: Username): JwtClaim = JwtClaim(
      subject = Some(username),
      expiration = Some(Instant.now.plusSeconds(durationInSecs).getEpochSecond),
      issuedAt = Some(Instant.now.getEpochSecond)
    )

    val secretKey: SecretKeySpec = new SecretKeySpec(
      key.getBytes("UTF-8"), algorithm.name
    )

object JwtConfig:
  private val defaultKey: String = "change-me-in-production"
  private val defaultAlgorithm: JwtAlgorithm = HS512
  private val defaultDurationInSecs: Int = 900
  
  private type Env = String & JwtAlgorithm & Int

  val customLayer: RLayer[Env, JwtConfig] =
    ZLayer.fromFunction(JwtConfig(_, _, _))

  val defaultLayer: ULayer[JwtConfig] =
    ZLayer.succeed(JwtConfig(defaultKey, defaultAlgorithm, defaultDurationInSecs))