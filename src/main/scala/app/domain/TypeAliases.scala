package app.domain

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

type MovieIdDescription = DescribedAs[Greater[0], "Id must be greater than 0"]
type MovieId = Long :| MovieIdDescription

type UserIdDescription = DescribedAs[Greater[0], "Id must be greater than 0"]
type UserId = Long :| UserIdDescription