package domain

import com.augustnagro.magnum.*

import io.circe.*
import io.circe.generic.semiauto.*

@Table(PostgresDbType, SqlNameMapper.CamelToSnakeCase)
case class User(
               @Id uid: Long,
               username: String,
               passwordHash: String
               ) derives DbCodec, Codec.AsObject

val userRepo = Repo[User, User, Long]


