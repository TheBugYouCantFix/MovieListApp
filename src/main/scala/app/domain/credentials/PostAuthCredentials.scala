package app.domain.credentials

import app.domain.{UserId, Password}
import app.utils.given
import io.circe.*

case class PostAuthCredentials(
                               userId: UserId,
                               password: Password
                             ) derives Codec.AsObject
given Conversion[PostAuthCredentials, Credentials] = c => Credentials(c.userId, c.password)
