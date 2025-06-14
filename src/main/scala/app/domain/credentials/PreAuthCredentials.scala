package app.domain.credentials

import app.domain.{Username, Password}
import app.utils.given
import io.circe.*

import scala.language.implicitConversions

case class PreAuthCredentials(
                               username: Username,
                               password: Password
                             ) derives Codec.AsObject

given Conversion[PreAuthCredentials, Credentials] = c => Credentials(c.username, c.password)