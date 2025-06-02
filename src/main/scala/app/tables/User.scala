package app.tables

import com.augustnagro.magnum.*

import app.domain
import app.domain.UserId

@Table(PostgresDbType, SqlNameMapper.CamelToSnakeCase)
final case class User(
               @Id uid: UserId,
               username: domain.Username,
               passwordHash: String
               ):
  val toDomain: domain.User = domain.User(username, passwordHash)
  
object User:
  val table = TableInfo[domain.User, User, UserId]
  def fromDomain(id: UserId, user: domain.User): User =
    User(id, user.username, user.passwordHash)



