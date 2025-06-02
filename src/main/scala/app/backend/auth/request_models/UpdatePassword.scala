package app.backend.auth.request_models

import io.circe.*

import app.domain.{Credentials, Username, Password}
import app.utils.given

case class UpdatePassword(
                             credentials: Credentials,
                             password: Password
                             ) derives Codec.AsObject


