package domain

import io.circe.*
import io.circe.generic.semiauto.*

case class Error(message: String) derives Codec.AsObject


