package app.backend.auth.requestmodels

import io.circe.*

import app.utils.given
import app.domain.{Password, Username, UserId}

case class UpdateUsernameRequest(
                         password: Password,
                         newUsername: Username
                         ) derives Codec.AsObject

case class UpdateUsernameOperation(
                                  userId: UserId,
                                  newUsername: Username
                                  ) derives Codec.AsObject