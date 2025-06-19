package app.backend.data.repositories

import zio.*
import com.augustnagro.magnum.magzio.*

import app.domain.{Movie, MovieId, UserId}
import app.{domain, tables}
import app.tables.Movies
import app.utils.given

trait MovieRepo:
  def add(movie: Movie): Task[Unit]
  def getById(id: MovieId): Task[Option[Movie]]
  def getAll(userId: UserId): Task[Vector[Movie]]
  def updateTo(id: MovieId, movie: Movie): Task[Unit]
  def removeById(id: MovieId): Task[Unit]

final case class MovieRepoLive(xa: Transactor) extends Repo[Movie, Movies, MovieId] with MovieRepo:
  override def add(movie: Movie): Task[Unit] =
    xa.transact {
      insert(movie)
    }

  override def getById(id: MovieId): Task[Option[Movie]] =
    xa.transact {
      findById(id).map(_.toDomain)
    }

  override def getAll(userId: UserId): Task[Vector[Movie]] = {
    val spec = Spec[Movies]
      .where(sql"${tables.Movies.table.uid} = $userId")
    
    xa.transact {
      findAll(spec).map(_.toDomain)
    }
  }

  override def updateTo(id: MovieId, movie: Movie): Task[Unit] =
    xa.transact {
     update(tables.Movies.fromDomain(id, movie))
    }

  override def removeById(id: MovieId): Task[Unit] =
    xa.transact {
      deleteById(id)
    }

object MovieRepo:
  val layer: RLayer[Transactor, MovieRepo] =
    ZLayer.fromFunction(MovieRepoLive(_))
    