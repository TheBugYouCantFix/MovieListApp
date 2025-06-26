package app.domain

import app.domain.{MovieId, UserId}

sealed trait AppError extends Throwable:
  val message: String

enum MovieError(val message: String) extends AppError:
  case NotFound(movieId: MovieId) extends MovieError(s"No movie with id $movieId")

enum UserError(val message: String) extends AppError:
  case NotFound(userId: UserId) extends UserError(s"No user with id $userId")

enum AuthError(val message: String) extends AppError:
  case InvalidCredentials() extends AuthError("Invalid credentials")
  case PasswordHashingFailed() extends AuthError("Password hashing failed")

enum DbError(val message: String) extends AppError:
  case UnexpectedDbError(msg: String) extends DbError(s"Something went wrong with the db: $msg")