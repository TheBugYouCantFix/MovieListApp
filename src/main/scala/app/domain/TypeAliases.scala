package app.domain

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

type BaseIdType = Long

type MovieIdDescription = DescribedAs[Greater[0], "Id must be greater than 0"]
type MovieId = BaseIdType :| MovieIdDescription
object MovieId extends RefinedType[BaseIdType, MovieIdDescription]

type UserIdDescription = DescribedAs[Greater[0], "Id must be greater than 0"]
type UserId = BaseIdType :| UserIdDescription
object UserId extends RefinedType[BaseIdType, UserIdDescription]

type PasswordDescription = DescribedAs[MinLength[5], "Min password length is 5"]
type Password = String :| PasswordDescription
object Password extends RefinedType[String, PasswordDescription]
