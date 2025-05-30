package app.domain

import io.circe.*

case class User(
                 username: String,
                 passwordHash: String
               ) derives Codec.AsObject

