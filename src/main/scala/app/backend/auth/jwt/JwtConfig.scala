package app.backend.auth.jwt

import pdi.jwt.{JwtAlgorithm, JwtClaim}
import javax.crypto.spec.SecretKeySpec
import java.time.Instant

import app.domain.UserId

case class JwtConfig(
                      userId: UserId,
                      key: String = "change-me-in-production",
                      algorithm: JwtAlgorithm = JwtAlgorithm.HS512
                    ):
    val claim: JwtClaim = JwtClaim(
      subject = Some(userId.toString),
      expiration = Some(Instant.now.plusSeconds(900).getEpochSecond),
      issuedAt = Some(Instant.now.getEpochSecond)
    )
    
    val secretKey: SecretKeySpec = new SecretKeySpec(
      key.getBytes("UTF-8"), algorithm.name
    )

