package app.domain

import io.circe.*
import io.circe.generic.auto.*
import sttp.tapir.Schema

import app.domain.Username
import app.utils.given

case class Credentials(username: Username, passwordHash: String) derives Codec.AsObject


