package backend.data.repositories

import zio.*
import com.augustnagro.magnum.magzio.*
import com.augustnagro.magnum.magzio.Transactor.*

import javax.sql.DataSource

import domain.{ID, Movie}
import tables.*

trait MovieRepo:
  def add(movie: Movie): Task[Unit]
  def getById(id: ID): Task[Option[Movie]]
  def getAll: Task[Vector[Movie]]
  def updateMovie(id: ID, movie: Movie): Task[Unit]
  def removeById(id: ID): Task[Unit] // name deleteById would shadow the db codec method name

final case class MovieRepoLive(xa: Transactor) extends Repo[Movie, tables.Movie, ID] with MovieRepo:
  override def add(movie: Movie): Task[Unit] =
    xa.transact {
      insert(movie)
    }

  override def getById(id: ID): Task[Option[Movie]] = 
    xa.transact {
      findById(id).map(_.toDomain)
    }

  override def getAll: Task[Vector[Movie]] =
    xa.transact {
      findAll.map(_.toDomain)
    }

  override def updateMovie(id: ID, movie: Movie): Task[Unit] = 
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
    