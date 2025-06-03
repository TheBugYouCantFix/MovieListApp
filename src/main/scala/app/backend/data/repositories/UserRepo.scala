package app.backend.data.repositories

import app.domain.{UserId, Username}
import app.{domain, tables}
import app.tables.User
import app.utils.given

import zio.*
import com.augustnagro.magnum.magzio.*

trait UserRepo:
  def add(user: domain.User): Task[Unit]
  def getById(id: UserId): Task[Option[domain.User]]
  def updateUsername(id: UserId, username: Username, user: domain.User): Task[Unit]
  def updatePasswordHash(id: UserId, passwordHash: String, user: domain.User): Task[Unit]
  def removeById(id: UserId): Task[Unit]
  def getUidByCredentials(username: Username, passwordHash: String): Task[Option[UserId]]

final case class UserRepoLive(xa: Transactor) extends Repo[domain.User, User, UserId] with UserRepo:
  override def add(user: domain.User): Task[Unit] =
    xa.transact {
      insert(user)
    }

  override def getById(id: UserId): Task[Option[domain.User]] =
    xa.transact {
      findById(id).map(_.toDomain)
    }

  def updateTo(id: UserId, user: domain.User): Task[Unit] =
    xa.transact {
      update(tables.User.fromDomain(id, user))
    }

  override def updateUsername(id: UserId, username: Username, user: domain.User): Task[Unit] =
    updateTo(id, user.copy(username = username))

  override def updatePasswordHash(id: UserId, passwordHash: String, user: domain.User): Task[Unit] = 
    updateTo(id, user.copy(passwordHash = passwordHash))
    
  override def removeById(id: UserId): Task[Unit] =
    xa.transact {
      deleteById(id)
    }

  override def getUidByCredentials(username: Username, passwordHash: String): Task[Option[UserId]] =
     xa.transact {
      val frag =
        sql"""
          SELECT uid FROM ${tables.User.table} 
          WHERE username = $username AND password_hash = $passwordHash
          LIMIT 1
          """

      frag.query[Option[UserId]].run().head
    }



object UserRepoLive:
  val layer: RLayer[Transactor, UserRepo] =
    ZLayer.fromFunction(UserRepoLive(_))

