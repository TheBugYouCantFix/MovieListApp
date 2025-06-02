package app.backend.auth.request_models

import io.circe.*

import app.domain.{Credentials, UserId}
import app.utils.given

case class DeleteUser(
                     credentials: Credentials,
                     userId: UserId 
                     ) derives Codec.AsObject
