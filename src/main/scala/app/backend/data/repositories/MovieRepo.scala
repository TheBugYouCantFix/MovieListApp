package app.backend.data.repositories

import app.domain.MovieId
import app.{domain, tables}
import app.tables.Movie
import app.utils.given 

import zio.*
import com.augustnagro.magnum.magzio.*

trait MovieRepo:
  def add(movie: domain.Movie): Task[Unit]
  def getById(id: MovieId): Task[Option[domain.Movie]]
  def getAll: Task[Vector[domain.Movie]]
  def updateTo(id: MovieId, movie: domain.Movie): Task[Unit]
  def removeById(id: MovieId): Task[Unit]

final case class MovieRepoLive(xa: Transactor) extends Repo[domain.Movie, Movie, MovieId] with MovieRepo:
  override def add(movie: domain.Movie): Task[Unit] =
    xa.transact {
      insert(movie)
    }

  override def getById(id: MovieId): Task[Option[domain.Movie]] =
    xa.transact {
      findById(id).map(_.toDomain)
    }

  override def getAll: Task[Vector[domain.Movie]] =
    xa.transact {
      findAll.map(_.toDomain)
    }

  override def updateTo(id: MovieId, movie: domain.Movie): Task[Unit] =
    xa.transact {
     update(tables.Movie.fromDomain(id, movie))
    }

  override def removeById(id: MovieId): Task[Unit] =
    xa.transact {
      deleteById(id)
    }


object MovieRepoLive:
  val layer: RLayer[Transactor, MovieRepo] =
    ZLayer.fromFunction(MovieRepoLive(_))
    