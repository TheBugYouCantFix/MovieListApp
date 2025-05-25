package app.tables

import app.domain
import com.augustnagro.magnum.*

@Table(PostgresDbType, SqlNameMapper.CamelToSnakeCase)
final case class User(
               @Id uid: domain.ID,
               username: String,
               passwordHash: String
               ):
  val toDomain: domain.User = domain.User(username, passwordHash)
  
object User:
  def fromDomain(id: domain.ID, user: domain.User): User =
    User(id, user.username, user.passwordHash)



