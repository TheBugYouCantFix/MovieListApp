package app.backend.auth.requestmodels

import io.circe.*

import app.domain.{Credentials, Username}
import app.utils.given 

case class UpdateUsername(
                          credentials: Credentials,
                          newUsername: Username
                         ) derives Codec.AsObject


