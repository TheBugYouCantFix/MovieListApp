package app.domain

import io.circe.*

import app.domain.Username
import app.utils.given

case class Credentials(
                        username: Username,
                        password: Password
                      ) derives Codec.AsObject
