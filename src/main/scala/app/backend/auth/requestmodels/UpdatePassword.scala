package app.backend.auth.requestmodels

import io.circe.*

import app.domain.{Credentials, Password}
import app.utils.given

case class UpdatePassword(
                             credentials: Credentials,
                             password: Password
                             ) derives Codec.AsObject


