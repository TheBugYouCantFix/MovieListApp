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
  def updateTo(id: UserId, user: domain.User): Task[Unit]
  def removeById(id: UserId): Task[Unit]
  def areCredentialsValid(username: Username, passwordHash: String): Task[Boolean]

final case class UserRepoLive(xa: Transactor) extends Repo[domain.User, User, UserId] with UserRepo:
  override def add(user: domain.User): Task[Unit] =
    xa.transact {
      insert(user)
    }

  override def getById(id: UserId): Task[Option[domain.User]] =
    xa.transact {
      findById(id).map(_.toDomain)
    }

  override def updateTo(id: UserId, user: domain.User): Task[Unit] =
    xa.transact {
      update(tables.User.fromDomain(id, user))
    }

  override def removeById(id: UserId): Task[Unit] =
    xa.transact {
      deleteById(id)
    }

  override def areCredentialsValid(username: Username, passwordHash: String): Task[Boolean] =
     xa.transact {
      val frag =
        sql"""
          SELECT EXISTS (
            SELECT 1 FROM users
            WHERE username = $username AND password_hash = $passwordHash
          )
          """

      frag.query[Boolean].run().head
    }



object UserRepoLive:
  val layer: RLayer[Transactor, UserRepo] =
    ZLayer.fromFunction(UserRepoLive(_))

