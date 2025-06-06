package app.backend.data.repositories

import app.domain.{NoUserWithGivenIdError, UserId, Username}
import app.{domain, tables}
import app.tables.Users
import app.utils.given
import zio.*
import com.augustnagro.magnum.magzio.*

trait UserRepo:
  def add(user: domain.User): Task[UserId]
  def getById(id: UserId): Task[Option[domain.User]]
  def updateUsername(id: UserId, username: Username): Task[Unit]
  def updatePasswordHash(id: UserId, passwordHash: String): Task[Unit]
  def removeById(id: UserId): Task[Unit]
  def getUidByCredentials(username: Username, passwordHash: String): Task[Option[UserId]]

final case class UserRepoLive(xa: Transactor) extends Repo[domain.User, Users, UserId] with UserRepo:
  override def add(user: domain.User): Task[UserId] =
    xa.transact {
      insertReturning(user).uid
    }

  override def getById(id: UserId): Task[Option[domain.User]] =
    xa.transact {
      findById(id).map(_.toDomain)
    }

  def updateTo(id: UserId, user: domain.User): Task[Unit] =
    xa.transact {
      update(tables.Users.fromDomain(id, user))
    }

  override def updateUsername(id: UserId, username: Username): Task[Unit] = 
    for 
      user <- getById(id)
      _ <- user match
        case Some(u) => updateTo(id, u.copy(username = username))
        case None => ZIO.fail(NoUserWithGivenIdError())
    yield ()

  override def updatePasswordHash(id: UserId, passwordHash: String): Task[Unit] =
    for
      user <- getById(id)
      _ <- user match
        case Some(u) => updateTo(id, u.copy(passwordHash = passwordHash))
        case None => ZIO.fail(NoUserWithGivenIdError())
    yield ()
    
  override def removeById(id: UserId): Task[Unit] =
    xa.transact {
      deleteById(id)
    }

  override def getUidByCredentials(username: Username, passwordHash: String): Task[Option[UserId]] =
     xa.transact {
      val frag =
        sql"""
          SELECT ${tables.Users.table.uid} FROM "${tables.Users.table}"
          WHERE username = $username AND password_hash = $passwordHash
          LIMIT 1
          """

      frag.query[UserId].run().headOption
    }



object UserRepo:
  val layer: RLayer[Transactor, UserRepo] =
    ZLayer.fromFunction(UserRepoLive(_))

