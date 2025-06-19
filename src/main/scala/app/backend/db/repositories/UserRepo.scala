package app.backend.db.repositories

import app.domain.{NoUserWithGivenIdError, UserId, Username, UserIdentifier}
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
  def getUidByUsername(username: Username): Task[Option[UserId]]
  def getPasswordHashByUsername(username: Username): Task[Option[String]]
  def getPasswordHashById(userId: UserId): Task[Option[String]]

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

  override def getUidByUsername(username: Username): Task[Option[UserId]] =
     xa.transact {
      val frag =
        sql"""
          SELECT ${tables.Users.table.uid} FROM "${tables.Users.table}"
          WHERE username = $username
          LIMIT 1
          """

      frag.query[UserId].run().headOption
    }

  override def getPasswordHashByUsername(username: Username): Task[Option[String]] =
    xa.transact {
      val frag =
        sql"""
          SELECT ${tables.Users.table.passwordHash} FROM "${tables.Users.table}"
          WHERE ${tables.Users.table.username} = $username
          LIMIT 1
          """

      frag.query[String].run().headOption
    }

  override def getPasswordHashById(userId: UserId): Task[Option[String]] =
    xa.transact {
      val frag =
        sql"""
          SELECT ${tables.Users.table.passwordHash} FROM "${tables.Users.table}"
          WHERE ${tables.Users.table.uid} = $userId
          LIMIT 1
          """

      frag.query[String].run().headOption
    }


object UserRepo:
  val layer: RLayer[Transactor, UserRepo] =
    ZLayer.fromFunction(UserRepoLive(_))

