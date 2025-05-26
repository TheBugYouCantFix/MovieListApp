package app.backend.data.repositories

import app.domain.ID
import app.{domain, tables}
import app.tables.User

import zio.*
import com.augustnagro.magnum.magzio.*
import com.augustnagro.magnum.magzio.Transactor.*

trait UserRepo:
  def add(user: domain.User): Task[Unit]
  def getById(id: ID): Task[Option[domain.User]]
  def getAll: Task[Vector[domain.User]]
  def updateTo(id: ID, user: domain.User): Task[Unit]
  def removeById(id: ID): Task[Unit]

final case class UserRepoLive(xa: Transactor) extends Repo[domain.User, User, ID] with UserRepo:
  override def add(user: domain.User): Task[Unit] =
    xa.transact {
      insert(user)
    }

  override def getById(id: ID): Task[Option[domain.User]] =
    xa.transact {
      findById(id).map(_.toDomain)
    }

  override def getAll: Task[Vector[domain.User]] =
    xa.transact {
      findAll.map(_.toDomain)
    }

  override def updateTo(id: ID, user: domain.User): Task[Unit] =
    xa.transact {
      update(tables.User.fromDomain(id, user))
    }

  override def removeById(id: ID): Task[Unit] =
    xa.transact {
      deleteById(id)
    }


object UserRepoLive:
  val layer: RLayer[Transactor, UserRepo] =
    ZLayer.fromFunction(UserRepoLive(_))

