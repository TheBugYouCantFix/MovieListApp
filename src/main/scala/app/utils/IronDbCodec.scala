package app.utils

import com.augustnagro.magnum.DbCodec
import io.github.iltotore.iron.*

inline given ironDbCodec[T, Description](
  using DbCodec[T], Constraint[T, Description]
): DbCodec[T :| Description] =
  DbCodec[T].biMap(
    _.refineUnsafe[Description],
    identity
  )