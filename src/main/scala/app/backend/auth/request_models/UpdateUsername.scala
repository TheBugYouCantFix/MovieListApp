package app.backend.auth.request_models

import io.circe.*

import app.domain.{Credentials, Username}
import app.utils.given 

case class UpdateUsername(
                          credentials: Credentials,
                          username: Username
                         ) derives Codec.AsObject


