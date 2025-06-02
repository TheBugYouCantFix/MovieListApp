package app.backend.auth.jwt

import pdi.jwt.{JwtAlgorithm, JwtClaim}
import java.time.Instant

case class JwtConfig(
                    claim: JwtClaim = JwtClaim(
                      expiration = Some(Instant.now.plusSeconds(900).getEpochSecond),
                      issuedAt = Some(Instant.now.getEpochSecond)
                    ),
                    key: String = "change-me-in-production",
                    algorithm: JwtAlgorithm = JwtAlgorithm.HS256
                    )
