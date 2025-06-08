package app.backend.auth.requestmodels

import io.circe.*

import app.domain.{UserId, Username}
import app.utils.given 

case class UpdateUsername(
                         userId: UserId,
                          newUsername: Username
                         ) derives Codec.AsObject


