package app.backend.auth.requestmodels

import io.circe.*

import app.domain.{UserId, Password}
import app.utils.given

case class UpdatePassword(
                             userId: UserId, 
                             newPassword: Password
                             ) derives Codec.AsObject


