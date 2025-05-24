package domain

import io.circe.*
import io.circe.generic.semiauto.*

case class User(
                 username: String,
                 passwordHash: String
               ) derives Codec.AsObject

