package app.domain.credentials

import app.domain.{Username, Password}
import scala.language.implicitConversions

case class PreAuthCredentials(
                               username: Username,
                               password: Password
                             )
given Conversion[PreAuthCredentials, Credentials] = c => Credentials(c.username, c.password)