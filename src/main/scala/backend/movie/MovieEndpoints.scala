package backend.movie

import sttp.tapir.*
import sttp.tapir.json.circe.*
import sttp.tapir.generic.auto.*

import backend.data.dataSource
import com.augustnagro.magnum.connect

import domain.{Movie, movieRepo, Error}


object MovieEndpoints:
  val add = endpoint
    .post
    .in(jsonBody[Movie])
    .handleSuccess(
      connect(dataSource) {
        movieRepo.insert(_)
      }
    )

  val getById = endpoint
    .get
    .in(path[Long])
    .out(jsonBody[Movie])
    .errorOut(jsonBody[Error])
    .handle {
        connect(dataSource) {
          movieRepo.findById(_) match
            case None => Left(Error("No movie matches the given id"))
            case Some(m) => Right(m)
        }
    }

  val udpate = endpoint
    .put
    .in(jsonBody[Movie])
    .handleSuccess(
        connect(dataSource) {
          movieRepo.update(_)
        }
    )

  val delete = endpoint
    .delete
    .in(path[Long])
    .handleSuccess {
        connect(dataSource) {
          movieRepo.deleteById(_)
        }
    }