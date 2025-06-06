package app.backend.auth

import zio.*
import com.github.roundrop.bcrypt.*
import app.backend.data.repositories.UserRepo
import app.backend.auth.jwt.JwtService
import app.backend.auth.requestmodels.{UpdatePassword, UpdateUsername}
import app.domain.*
import app.backend.AppEnv


object AuthHandlers:
  private type AuthEnv = UserRepo & JwtService
  private def hashPassword(password: Password): ZIO[Any, PasswordHashingFailedError, String] =
    ZIO
      .fromTry(password.bcrypt())
      .mapError(e => PasswordHashingFailedError(e.getMessage))

  private def generateToken(username: Username): ZIO[JwtService, AuthError, String] =
    ZIO.serviceWithZIO[JwtService](
      _.jwtEncode(username)
    ).mapError(e => AuthError(e.getMessage))

  private def isPasswordCorrect(credentials: Credentials): ZIO[UserRepo, Error, Boolean] =
    for
      maybePassword <- ZIO.serviceWithZIO[UserRepo]
        (_.getPasswordHashByUsername(credentials.username))
        .mapError(e => AuthError(e.getMessage))

      res <- maybePassword match
        case Some(passwordHash: String) => ZIO.fromTry(
          credentials.password.isBcrypted(passwordHash)
        ).mapError(e => AuthError(e.getMessage))
        case None => ZIO.succeed(false)
    yield res

  private def executeIfPasswordCorrect[A, B](credentials: Credentials, arg: A, f: A => ZIO[AppEnv, Error, B]): ZIO[AppEnv, Error, B] =
    for
      ips <- isPasswordCorrect(credentials)
      res <- if ips then f(arg) else ZIO.fail(InvalidCredentialsError())
    yield res


  private def maybeUidFromUsername(username: Username): ZIO[AppEnv, Error, Option[UserId]] =
    for
      maybeUid <- ZIO.serviceWithZIO[UserRepo](_.getUidByUsername(
        username,
      )).mapError(e => AuthError(e.getMessage))
    yield maybeUid

  def loginHandler(credentials: Credentials): ZIO[AppEnv, Error, String] =
    executeIfPasswordCorrect[Username, String](credentials, credentials.username, generateToken)

  def signupHandler(credentials: Credentials): ZIO[AppEnv, Error, String] =
    for
      passwordHash <- hashPassword(credentials.password)

      uid <- ZIO.serviceWithZIO[UserRepo](_.add(
        User(credentials.username, passwordHash)
      )).mapError(e => AuthError(e.getMessage))

      token <- generateToken(credentials.username)
    yield token

  private def _updateUsernameHandler(updateUsername: UpdateUsername): ZIO[AppEnv, Error, Unit] =
    val creds = updateUsername.credentials
    for
      maybeUid <- maybeUidFromUsername(creds.username)
      uid <- ZIO.succeed(maybeUid).someOrFail(InvalidCredentialsError())
      _ <- ZIO.serviceWithZIO[UserRepo](_.updateUsername(
        uid, updateUsername.newUsername
      )).mapError(e => AuthError(e.getMessage))
    yield ()

  def updateUsernameHandler(updateUsername: UpdateUsername): ZIO[AppEnv, Error, Unit] =
    executeIfPasswordCorrect[UpdateUsername, Unit](updateUsername.credentials, updateUsername, _updateUsernameHandler)

  private def _updatePasswordHandler(updatePassword: UpdatePassword): ZIO[AppEnv, Error, Unit] =
    val creds = updatePassword.credentials
    for
      maybeUid <- maybeUidFromUsername(creds.username)
      uid <- ZIO.succeed(maybeUid).someOrFail(InvalidCredentialsError())
      newPasswordHash <- hashPassword(updatePassword.newPassword)
      _ <- ZIO.serviceWithZIO[UserRepo](_.updatePasswordHash(
        uid, newPasswordHash
      )).mapError(e => AuthError(e.getMessage))
    yield ()

  def updatePasswordHandler(updatePassword: UpdatePassword): ZIO[AppEnv, Error, Unit] =
    executeIfPasswordCorrect[UpdatePassword, Unit](updatePassword.credentials, updatePassword, _updatePasswordHandler)

  private def _deleteUserHandler(credentials: Credentials): ZIO[AppEnv, Error, Unit] =
    for
      maybeUid <- maybeUidFromUsername(credentials.username)
      uid <- ZIO.succeed(maybeUid).someOrFail(InvalidCredentialsError())
      _ <- ZIO.serviceWithZIO[UserRepo](_.removeById(uid))
        .mapError(e => AuthError(e.getMessage))
    yield ()

  def deleteUserHandler(credentials: Credentials): ZIO[AppEnv, Error, Unit] =
    executeIfPasswordCorrect[Credentials, Unit](credentials, credentials, _deleteUserHandler)
