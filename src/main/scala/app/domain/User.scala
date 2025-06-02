package app.domain

import io.circe.*

import app.domain.Username
import app.utils.given

case class User(
                 username: Username,
                 passwordHash: String
               ) derives Codec.AsObject

