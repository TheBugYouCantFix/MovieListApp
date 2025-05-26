package app.backend.data.repositories

import app.domain.ID
import app.{domain, tables}
import app.tables.Movie

import zio.*
import com.augustnagro.magnum.magzio.*
import com.augustnagro.magnum.magzio.Transactor.*

trait MovieRepo:
  def add(movie: domain.Movie): Task[Unit]
  def getById(id: ID): Task[Option[domain.Movie]]
  def getAll: Task[Vector[domain.Movie]]
  def updateTo(id: ID, movie: domain.Movie): Task[Unit]
  def removeById(id: ID): Task[Unit]

final case class MovieRepoLive(xa: Transactor) extends Repo[domain.Movie, Movie, ID] with MovieRepo:
  override def add(movie: domain.Movie): Task[Unit] =
    xa.transact {
      insert(movie)
    }

  override def getById(id: ID): Task[Option[domain.Movie]] =
    xa.transact {
      findById(id).map(_.toDomain)
    }

  override def getAll: Task[Vector[domain.Movie]] =
    xa.transact {
      findAll.map(_.toDomain)
    }

  override def updateTo(id: ID, movie: domain.Movie): Task[Unit] =
    xa.transact {
     update(tables.Movie.fromDomain(id, movie))
    }

  override def removeById(id: ID): Task[Unit] =
    xa.transact {
      deleteById(id)
    }


object MovieRepoLive:
  val layer: RLayer[Transactor, MovieRepo] =
    ZLayer.fromFunction(MovieRepoLive(_))
    