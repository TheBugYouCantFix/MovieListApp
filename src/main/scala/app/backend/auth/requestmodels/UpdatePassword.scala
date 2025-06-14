package app.backend.auth.requestmodels

import io.circe.*

import app.utils.given 
import app.domain.{Password, UserId}

// describes the HTTP request
case class UpdatePasswordRequest(
                           password: Password,
                           newPassword: Password 
                         ) derives Codec.AsObject

// describes the operations in the handler
case class UpdatePasswordOperation(
                                  userId: UserId,
                                  newPassword: Password
                                ) derives Codec.AsObject
