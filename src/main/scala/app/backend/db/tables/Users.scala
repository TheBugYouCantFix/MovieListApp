package app.backend.db.tables

import com.augustnagro.magnum.*

import app.domain
import app.domain.UserId

@Table(PostgresDbType, SqlNameMapper.CamelToSnakeCase)
final case class Users(
               @Id uid: UserId,
               username: domain.Username,
               passwordHash: String
               ):
  val toDomain: domain.User = domain.User(username, passwordHash)

object Users:
  val table = TableInfo[domain.User, Users, UserId]
  def fromDomain(id: UserId, user: domain.User): Users =
    Users(id, user.username, user.passwordHash)



