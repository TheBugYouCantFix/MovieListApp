package app.utils

import app.domain.{MovieId, MovieIdDescription, BaseIdType}
import com.augustnagro.magnum.DbCodec

import sttp.tapir.Schema

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import io.circe.*

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
given [A, C](using baseDecoder: Decoder[A], constraint: RuntimeConstraint[A, C]): Decoder[A :| C] =
  baseDecoder.emap(_.refineEither[C])

given [A, C](using baseEncoder: Encoder[A]): Encoder[A :| C] =
  baseEncoder.contramap(identity)