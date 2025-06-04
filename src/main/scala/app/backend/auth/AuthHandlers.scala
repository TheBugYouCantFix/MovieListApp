package app.backend.auth

import zio.*
import com.github.roundrop.bcrypt.bcrypt
import app.backend.data.repositories.UserRepo
import app.backend.auth.jwt.JwtService
import app.backend.auth.requestmodels.{UpdatePassword, UpdateUsername}
import app.domain.*


object AuthHandlers:
  private type AuthEnv = UserRepo & JwtService
  private def hashPassword(password: Password): ZIO[Any, PasswordHashingFailedError, String] =
    ZIO
      .fromTry(password.bcrypt())
      .mapError(e => PasswordHashingFailedError(e.getMessage))

  private def generateToken(userId: UserId): ZIO[JwtService, AuthError, String] =
    ZIO.serviceWithZIO[JwtService](
      _.jwtEncode(userId)
    ).mapError(e => AuthError(e.getMessage))

  private def maybeUidFromCredentials(credentials: Credentials): ZIO[AuthEnv, Error, (String, Option[UserId])] =
    for
      passwordHash <- hashPassword(credentials.password)

      maybeUid <- ZIO.serviceWithZIO[UserRepo](_.getUidByCredentials(
        credentials.username,
        passwordHash
      )).mapError(e => AuthError(e.getMessage))
    yield (passwordHash, maybeUid)

  def loginHandler(credentials: Credentials): ZIO[AuthEnv, Error, String] =
    for
      (_, maybeUid) <- maybeUidFromCredentials(credentials)

      token <- maybeUid match
        case Some(uid: UserId) => generateToken(uid)
        case None => ZIO.fail(InvalidCredentialsError())

    yield token

  def signupHandler(credentials: Credentials): ZIO[AuthEnv, Error, String] =
    for
      passwordHash <- hashPassword(credentials.password)

      uid <- ZIO.serviceWithZIO[UserRepo](_.add(
        User(credentials.username, passwordHash)
      )).mapError(e => AuthError(e.getMessage))

      token <- generateToken(uid)
    yield token

  def updateUsernameHandler(updateUsername: UpdateUsername): ZIO[AuthEnv, Error, Unit] = 
    val creds = updateUsername.credentials
    for
      (passwordHash, maybeUid) <- maybeUidFromCredentials(creds)
      uid <- ZIO.succeed(maybeUid).someOrFail(InvalidCredentialsError())
      _ <- ZIO.serviceWithZIO[UserRepo](_.updateUsername(
        uid, updateUsername.newUsername
      )).mapError(e => AuthError(e.getMessage))
    yield ()

  def updatePasswordHandler(updatePassword: UpdatePassword): ZIO[AuthEnv, Error, Unit] =
    val creds = updatePassword.credentials
    for
      (passwordHash, maybeUid) <- maybeUidFromCredentials(creds)
      uid <- ZIO.succeed(maybeUid).someOrFail(InvalidCredentialsError())
      newPasswordHash <- hashPassword(updatePassword.newPassword)
      _ <- ZIO.serviceWithZIO[UserRepo](_.updatePasswordHash(
        uid, newPasswordHash
      )).mapError(e => AuthError(e.getMessage))
    yield ()
    
  def deleteUserHandler(credentials: Credentials): ZIO[AuthEnv, Error, Unit] =
    for
      (_, maybeUid) <- maybeUidFromCredentials(credentials)
      uid <- ZIO.succeed(maybeUid).someOrFail(InvalidCredentialsError())
      _ <- ZIO.serviceWithZIO[UserRepo](_.removeById(uid))
        .mapError(e => AuthError(e.getMessage))
    yield ()
