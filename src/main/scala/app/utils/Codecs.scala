package app.utils

import app.domain.{MovieId, MovieIdDescription, BaseIdType}
import com.augustnagro.magnum.DbCodec

import sttp.tapir.*
import io.github.iltotore.iron.*
import sttp.tapir.CodecFormat.TextPlain

inline given ironDbCodec[T, Description](
  using DbCodec[T], Constraint[T, Description]
): DbCodec[T :| Description] =
  DbCodec[T].biMap(
    _.refineUnsafe[Description],
    identity
  )

given Codec[String, MovieId, TextPlain] =
  Codec.long.mapEither { long =>
    long.refineEither[MovieIdDescription] match {
      case Right(movieId: MovieId) => Right(movieId)
      case Left(error) => Left(s"Invalid MovieId: $error")
    }
  }(_.assume[BaseIdType])