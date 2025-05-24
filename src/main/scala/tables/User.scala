package tables

import com.augustnagro.magnum.*

import domain.*

@Table(PostgresDbType, SqlNameMapper.CamelToSnakeCase)
case class User(
               @Id uid: domain.ID,
               username: String,
               passwordHash: String
               ):
  val toDomain: domain.User = domain.User(username, passwordHash)
  
object User:
  def fromDomain(id: domain.ID, user: domain.User): User =
    User(id, user.username, user.passwordHash)



