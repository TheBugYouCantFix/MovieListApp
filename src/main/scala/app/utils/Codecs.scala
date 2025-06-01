package app.utils

import app.domain.{MovieId, MovieIdDescription, BaseIdType}
import com.augustnagro.magnum.DbCodec

import sttp.tapir.{Schema, Codec, CodecFormat}

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


// circe decoder codec
given [A, C](using baseDecoder: Decoder[A], constraint: RuntimeConstraint[A, C]): Decoder[A :| C] =
  baseDecoder.emap(_.refineEither[C])

// circe encoder codec
given [A, C](using baseEncoder: Encoder[A]): Encoder[A :| C] =
  baseEncoder.contramap(identity)

// tapir schema codec
given [A, C](using baseSchema: Schema[A], constraint: RuntimeConstraint[A, C]): Schema[A :| C] =
  baseSchema.map(Some(_))(_.refineUnsafe[C]).as[A :| C]

// tapir codec for types in paths
given [A, C](using
             baseCodec: Codec[String, A, CodecFormat.TextPlain],
             constraint: RuntimeConstraint[A, C]
            ): Codec[String, A :| C, CodecFormat.TextPlain] =
baseCodec.mapEither(_.refineEither[C])(identity)